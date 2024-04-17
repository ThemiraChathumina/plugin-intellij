package io.ballerina.plugins.idea.runconfig;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class BallerinaRunState extends CommandLineState {

    protected final List<String> commands;
    protected final String balPath;
    protected int port;
    protected boolean isDebugging = false;
    protected String script;

    protected BallerinaRunState(ExecutionEnvironment environment, String balPath, String script,
                                List<String> commands) {
        super(environment);
        this.commands = commands;
        this.balPath = balPath;
        this.script = script;
    }

    public void enableDebugging() {
        isDebugging = true;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getScript() {
        return script;
    }
}
