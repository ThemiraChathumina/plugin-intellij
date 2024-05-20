package io.ballerina.plugins.idea.debugger;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.util.net.NetUtils;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import io.ballerina.plugins.idea.runconfig.BallerinaExecutionState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.ServerSocket;

/**
 * Debugger runner which provides debugging capability.
 */
public class BallerinaDebugger extends GenericProgramRunner {

    private static final String ID = "BallerinaDebugger";

    private static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (Exception ignore) {
        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start debugging");
    }

    @NotNull
    @Override
    public String getRunnerId() {
        return ID;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return DefaultDebugExecutor.EXECUTOR_ID.equals(executorId);
    }

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env)
            throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();
        BallerinaExecutionState ballerinaExecutionState = (BallerinaExecutionState) state;
        int port = findFreePort();
        ballerinaExecutionState.allowDebuggingWithPort(port);
        FileDocumentManager.getInstance().saveAllDocuments();
        return XDebuggerManager.getInstance(env.getProject()).startSession(env, new XDebugProcessStarter() {

            @NotNull
            @Override
            public XDebugProcess start(@NotNull XDebugSession session) throws ExecutionException {
                // Get the host address.
                String host = NetUtils.getLocalHostString();
                // We need to pass the debug entry point to the debug adapter.
                String entryFilePath = ballerinaExecutionState.getScript();
                // use Path to get correct path from entryFilePath
                // Create a new connector. This will be used to communicate with the debugger.
                BallerinaDAPClientConnector ballerinaDebugSession =
                        new BallerinaDAPClientConnector(env.getProject(), entryFilePath, host, port);
                BallerinaDebugProcess process =
                        new BallerinaDebugProcess(session, ballerinaDebugSession, getExecutionResults(state, env));
//                    BallerinaDebugProcess process = new BallerinaDebugProcess(session, ballerinaDebugSession, null);
                ballerinaDebugSession.setContext(process);
                return process;
            }
        }).getRunContentDescriptor();

//        else if (state instanceof BallerinaTestRunningState) {
//            FileDocumentManager.getInstance().saveAllDocuments();
//            BallerinaHistoryProcessListener historyProcessListener = new BallerinaHistoryProcessListener();
//            int port = findFreePort();
//
//            FileDocumentManager.getInstance().saveAllDocuments();
//            ((BallerinaTestRunningState) state).setHistoryProcessHandler(historyProcessListener);
//            ((BallerinaTestRunningState) state).setDebugPort(port);
//
//            return XDebuggerManager.getInstance(env.getProject()).startSession(env, new XDebugProcessStarter() {
//
//                @NotNull
//                @Override
//                public XDebugProcess start(@NotNull XDebugSession session) throws ExecutionException {
//                    // Get the host address.
//                    String host = NetUtils.getLocalHostString();
//                    // We need to pass the debug entry point to the debug adapter.
//                    String entryFilePath = ((BallerinaTestRunningState) state).getConfiguration().getFilePath();
//                    // Create a new connector. This will be used to communicate with the debugger.
//                    BallerinaDAPClientConnector ballerinaDebugSession = new BallerinaDAPClientConnector(
//                            env.getProject(), entryFilePath, host, port);
//                    BallerinaDebugProcess process = new BallerinaDebugProcess(session, ballerinaDebugSession,
//                            getExecutionResults(state, env));
//                    ballerinaDebugSession.setContext(process);
//                    return process;
//                }
//            }).getRunContentDescriptor();
//        } else if (state instanceof BallerinaRemoteRunningState) {
//            FileDocumentManager.getInstance().saveAllDocuments();
//            return XDebuggerManager.getInstance(env.getProject()).startSession(env, new XDebugProcessStarter() {
//
//                @NotNull
//                @Override
//                public XDebugProcess start(@NotNull XDebugSession session) throws ExecutionException {
//                    // Get the remote host address.
//                    Pair<String, Integer> address = getRemoteAddress(env);
//                    if (address == null || address.getLeft().isEmpty()) {
//                        throw new ExecutionException("Invalid remote address.");
//                    }
//                    // We need to pass the debug entry point to the debug adapter.
//                    String entryFilePath = ((BallerinaRemoteRunningState) state).getConfiguration().getFilePath();
//                    // Create a new connector. This will be used to communicate with the debugger.
//                    BallerinaDAPClientConnector ballerinaDebugSession = new BallerinaDAPClientConnector(
//                            env.getProject(), entryFilePath, address.getLeft(), address.getRight());
//                    BallerinaDebugProcess process = new BallerinaDebugProcess(session, ballerinaDebugSession, null);
//                    ballerinaDebugSession.setContext(process);
//                    return process;
//                }
//            }).getRunContentDescriptor();
//        }
    }

//    @Nullable
//    private Pair<String, Integer> getRemoteAddress(@NotNull ExecutionEnvironment env) {
//        RunnerAndConfigurationSettings runnerAndConfigurationSettings = env.getRunnerAndConfigurationSettings();
//        if (runnerAndConfigurationSettings == null) {
//            return null;
//        }
//        RunConfiguration configurationSettings = runnerAndConfigurationSettings.getConfiguration();
//        if (configurationSettings instanceof BallerinaRemoteConfiguration) {
//            BallerinaRemoteConfiguration applicationConfiguration =
//                    (BallerinaRemoteConfiguration) configurationSettings;
//            String remoteDebugHost = applicationConfiguration.getRemoteDebugHost();
//            if (remoteDebugHost.isEmpty()) {
//                return null;
//            }
//            try {
//                int remoteDebugPort = Integer.parseInt(applicationConfiguration.getRemoteDebugPort());
//                return new ImmutablePair<>(remoteDebugHost, remoteDebugPort);
//            } catch (NumberFormatException e) {
//                return null;
//            }
//        }
//        return null;
//    }

    private ExecutionResult getExecutionResults(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env)
            throws ExecutionException {
        // Start debugger.
        ExecutionResult executionResult = state.execute(env.getExecutor(), new BallerinaDebugger());
        if (executionResult == null) {
            throw new ExecutionException("Cannot run debugger");
        }
        return executionResult;
    }
}
