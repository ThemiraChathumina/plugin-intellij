package org.intellij.sdk.language.debugger.client.connection;


import com.intellij.openapi.diagnostic.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

/**
 * Socket based stream connection provider.
 */
public class BallerinaSocketStreamConnectionProvider extends BallerinaProcessStreamConnectionProvider {

    private static final Logger LOG = Logger.getInstance(BallerinaSocketStreamConnectionProvider.class);
    private String address;
    private int port;

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public BallerinaSocketStreamConnectionProvider(List<String> commands, String workingDir, String address, int port) {
        super(commands, workingDir);
        this.address = address;
        this.port = port;
    }

    @Override
    public void start() throws IOException {
        Thread socketThread = new Thread(() -> {
            try {
                System.out.println("Connecting to the debug server at " + address + ":" + port + "...");
                socket = new Socket(address, port);
                System.out.println(socket.isConnected());
            } catch (Exception e) {
                System.out.println("Error occurred while connecting to the debug server.");
                LOG.warn(e);
            }
        });

        Thread adapterLauncherThread = new Thread(() -> {
            try {
                super.start();
                InputStream stdIn = super.getInputStream();
                if (stdIn == null) {
                    throw new IOException("Debug adapter input stream is null.");
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdIn), 1);
                String line;
                while ((line = bufferedReader.readLine()) != null && line.contains("Debug server started")) {
                    // Just waits here for the debug adapter to print server init message to the std out.
                    System.out.println(line);
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                LOG.info(e);
            }
        });

        adapterLauncherThread.start();
        try {
            adapterLauncherThread.join(4000);
        } catch (InterruptedException e) {
            LOG.warn(e);
        }
        socketThread.start();
        try {
            socketThread.join(4000);
        } catch (InterruptedException e) {
            LOG.warn(e);
        }
        if (socket == null) {
            inputStream = null;
            outputStream = null;
            throw new IOException("Unable to make socket connection: " + toString());
        } else {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        }

        // Kills process stream connection as the only socket connection will be used for the communication.
        super.stop();
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    // Todo - Implement

    //    @Override
    //    public InputStream getErrorStream() {
    //        inputStream;
    //    }

    @Override
    public void stop() {
        super.stop();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        return result ^ Objects.hashCode(this.port);
    }
}
