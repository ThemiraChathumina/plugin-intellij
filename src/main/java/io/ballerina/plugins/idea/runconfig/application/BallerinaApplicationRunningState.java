package io.ballerina.plugins.idea.runconfig.application;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import io.ballerina.plugins.idea.runconfig.BallerinaRunState;
import io.ballerina.plugins.idea.sdk.BallerinaSdkDetection;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class BallerinaApplicationRunningState extends BallerinaRunState {

    protected BallerinaApplicationRunningState(ExecutionEnvironment environment, String balPath, String script,
                                               List<String> commands) {
        super(environment, balPath, script, commands);
    }

    @Override
    protected @NotNull ProcessHandler startProcess() throws ExecutionException {
        String ballerinaPackage = BallerinaSdkDetection.findBallerinaPackage(script);
        if (!ballerinaPackage.isEmpty()) {
            script = ballerinaPackage;
        }

        String lastPath = Paths.get(script).normalize().getFileName().toString();
        String parentPath = Paths.get(script).normalize().getParent().toString();

        GeneralCommandLine commandLine = new GeneralCommandLine(balPath, "run");

        if (isDebugging) {
            commandLine.addParameter("--debug");
            commandLine.addParameter(Integer.toString(port));
        }

        commandLine.addParameter(lastPath);

        if (commands != null) {
            for (String cmd : commands) {
                if (!Objects.equals(cmd, "")) {
                    commandLine.addParameter(cmd);
                }
            }
        }

        commandLine.setWorkDirectory(parentPath);
        OSProcessHandler processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine);
        ProcessTerminatedListener.attach(processHandler);
        return processHandler;
    }
}
