package io.ballerina.plugins.idea.runconfig.test;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import io.ballerina.plugins.idea.project.BallerinaProjectUtil;
import io.ballerina.plugins.idea.runconfig.BallerinaRunState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class BallerinaTestState extends BallerinaRunState {

    protected BallerinaTestState(ExecutionEnvironment environment, String balPath, String script,
                                 List<String> commands) {
        super(environment, balPath, script, commands);
    }

    @Override
    protected @NotNull ProcessHandler startProcess() throws ExecutionException {
        String ballerinaPackage = BallerinaProjectUtil.findBallerinaPackage(script);
        if (!ballerinaPackage.isEmpty()) {
            script = ballerinaPackage;
        }

        GeneralCommandLine commandLine = new GeneralCommandLine(balPath, "test");

        if (isDebugging) {
            commandLine.addParameter("--debug");
            commandLine.addParameter(Integer.toString(port));
        }

        if (commands != null) {
            for (String cmd : commands) {
                if (!Objects.equals(cmd, "")) {
                    commandLine.addParameter(cmd);
                }
            }
        }

        commandLine.setWorkDirectory(script);
        OSProcessHandler processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine);
        ProcessTerminatedListener.attach(processHandler);
        return processHandler;
    }
}
