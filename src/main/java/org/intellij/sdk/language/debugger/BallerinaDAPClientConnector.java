package org.intellij.sdk.language.debugger;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.lsp4j.debug.Capabilities;
import org.eclipse.lsp4j.debug.DisconnectArguments;
import org.eclipse.lsp4j.debug.InitializeRequestArguments;
import org.eclipse.lsp4j.debug.launch.DSPLauncher;
import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.intellij.sdk.language.debugger.client.DAPClient;
import org.intellij.sdk.language.debugger.client.DAPRequestManager;
import org.intellij.sdk.language.debugger.client.connection.BallerinaSocketStreamConnectionProvider;
import org.intellij.sdk.language.debugger.client.connection.BallerinaStreamConnectionProvider;
import org.jetbrains.annotations.NotNull;
import org.wso2.lsp4intellij.utils.OSUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;


/**
 * Used to communicate with debug server.
 */
public class BallerinaDAPClientConnector {

    private static final Logger LOG = Logger.getInstance(BallerinaDAPClientConnector.class);

    private BallerinaDebugProcess context;
    private final Project project;
    private final String entryFilePath;
    private final String host;
    private final int port;
    private DAPClient debugClient;
    private IDebugProtocolServer debugServer;
    private DAPRequestManager requestManager;
    private BallerinaStreamConnectionProvider streamConnectionProvider;
    private Future<Void> launcherFuture;
    private CompletableFuture<Capabilities> initializeFuture;
    private Capabilities initializeResult;
    private ConnectionState myConnectionState;

    private final int debugAdapterPort;
    private static final String CONFIG_SOURCE = "script";
    private static final String CONFIG_DEBUGEE_HOST = "debuggeeHost";
    private static final String CONFIG_DEBUGEE_PORT = "debuggeePort";

    public BallerinaDAPClientConnector(@NotNull Project project, @NotNull String entryFilePath, @NotNull String host,
                                       int port) {
        this.project = project;
        this.entryFilePath = entryFilePath;
        this.host = host;
        this.port = port;
        this.debugAdapterPort = findFreePort();
        myConnectionState = ConnectionState.NOT_CONNECTED;
    }

    public DAPRequestManager getRequestManager() {
        return requestManager;
    }

    public Project getProject() {
        return project;
    }

    public int getPort() {
        return port;
    }

    public BallerinaDebugProcess getContext() {
        return context;
    }

    public void setContext(BallerinaDebugProcess context) {
        this.context = context;
    }

    void createConnection() {
        try {
            myConnectionState = ConnectionState.CONNECTING;
            streamConnectionProvider = createConnectionProvider(project);
            if (streamConnectionProvider == null) {
                LOG.warn("Unable to establish the socket connection provider.");
                return;
            }
            streamConnectionProvider.start();
            Pair<InputStream, OutputStream> streams = new ImmutablePair<>(streamConnectionProvider.getInputStream(),
                    streamConnectionProvider.getOutputStream());
            InputStream inputStream = streams.getKey();
            OutputStream outputStream = streams.getValue();

            if (inputStream == null || outputStream == null) {
                LOG.warn("Unable to establish connection with the debug server.");
                return;
            }

            debugClient = new DAPClient();
            Launcher<IDebugProtocolServer> clientLauncher = DSPLauncher.createClientLauncher(debugClient,
                    inputStream, outputStream);
            debugServer = clientLauncher.getRemoteProxy();
            launcherFuture = clientLauncher.startListening();

            InitializeRequestArguments initParams = new InitializeRequestArguments();
            initParams.setAdapterID("BallerinaDebugClient");

            initializeFuture = debugServer.initialize(initParams).thenApply(res -> {
                initializeResult = res;
                LOG.info("initialize response received from the debug server.");
                debugClient.initialized();
                requestManager = new DAPRequestManager(this, debugClient, debugServer, initializeResult);
                debugClient.connect(requestManager);
                myConnectionState = ConnectionState.CONNECTED;
                return res;
            });

        } catch (Exception e) {
            myConnectionState = ConnectionState.NOT_CONNECTED;
            LOG.warn("Error occurred when trying to initialize connection with the debug server.", e);
        }
    }

    @NotNull
    public String getAddress() {
        return String.format("host:%s and port: %d", host, port);
    }

    void attachToServer() {
        try {
            Map<String, Object> requestArgs = new HashMap<>();
            requestArgs.put(CONFIG_SOURCE, entryFilePath);
            requestArgs.put(CONFIG_DEBUGEE_HOST, host);
            requestArgs.put(CONFIG_DEBUGEE_PORT, Integer.toString(port));
            System.out.println("entryFilePath: " + entryFilePath);
            requestManager.attach(requestArgs);
            System.out.println("attach request sent to the debug server.");
        } catch (Exception e) {
            LOG.warn("Attaching to the debug adapter failed", e);
        }
    }

    void disconnectFromServer() throws Exception {
        try {
            DisconnectArguments disconnectArgs = new DisconnectArguments();
            disconnectArgs.setTerminateDebuggee(false);
            requestManager.disconnect(disconnectArgs);
            stop();
        } catch (Exception e) {
            LOG.warn("Disconnecting from the debug adapter failed", e);
            throw e;
        }
    }

    public boolean isConnected() {
        return debugClient != null && launcherFuture != null && !launcherFuture.isCancelled()
                && myConnectionState == ConnectionState.CONNECTED;
    }

    void stop() {
        streamConnectionProvider.stop();
        myConnectionState = ConnectionState.NOT_CONNECTED;
    }

    String getState() {
        if (myConnectionState == ConnectionState.NOT_CONNECTED) {
            return "Not connected. Waiting for a connection.";
        } else if (myConnectionState == ConnectionState.CONNECTED) {
            return "Connected to " + getAddress() + ".";
        } else if (myConnectionState == ConnectionState.DISCONNECTED) {
            return "Disconnected.";
        } else if (myConnectionState == ConnectionState.CONNECTING) {
            return "Connecting to " + getAddress() + ".";
        }
        return "Unknown";
    }

    private enum ConnectionState {
        NOT_CONNECTED, CONNECTING, CONNECTED, DISCONNECTED
    }

    private BallerinaStreamConnectionProvider createConnectionProvider(Project project) {

        String debugLauncherPath = "C:\\Program Files\\Ballerina\\distributions\\ballerina-2201.8.4\\bin\\bal.bat";
        String command = "start-debugger-adapter";
        List<String> processArgs = new ArrayList<>();
        processArgs.add(debugLauncherPath);
        processArgs.add(command);
        processArgs.add(Integer.toString(debugAdapterPort));
        System.out.println(processArgs);
        return new BallerinaSocketStreamConnectionProvider(processArgs, project.getBasePath(), host, debugAdapterPort);
    }

    private static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (Exception ignore) {
        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start debugging");
    }
}
