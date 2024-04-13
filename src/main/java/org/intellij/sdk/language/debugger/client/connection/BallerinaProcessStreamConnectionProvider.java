package org.intellij.sdk.language.debugger.client.connection;


import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * A class symbolizing a stream to a process.
 * <p>
 * commands - The commands to start the process
 * workingDir - The working directory of the process
 */
public class BallerinaProcessStreamConnectionProvider implements BallerinaStreamConnectionProvider {

    private static final Logger LOG = Logger.getInstance(BallerinaProcessStreamConnectionProvider.class);
    private List<String> commands;
    private String workingDir;

    public BallerinaProcessStreamConnectionProvider(List<String> commands, String workingDir) {
        this.commands = commands;
        this.workingDir = workingDir;
    }

    @Nullable
    private Process process = null;

    public void start() throws IOException {
        if (workingDir == null || commands == null || commands.isEmpty() || commands.contains(null)) {
            throw new IOException("Unable to start debug server: " + this.toString());
        }
        ProcessBuilder builder = createProcessBuilder();
        LOG.info("Starting server process with commands " + commands + " and workingDir " + workingDir);
        process = builder.start();
        if (!process.isAlive()) {
            throw new IOException("Unable to start debug server: " + this.toString());
        } else {
            LOG.info("Server process started " + process);
        }
    }

    private ProcessBuilder createProcessBuilder() {
        //TODO for cquery, REMOVE
        commands.forEach(c -> c = c.replace("\'", ""));
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.directory(new File(workingDir));
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        return builder;
    }

    @Nullable
    @Override
    public InputStream getInputStream() {
        return process != null ? process.getInputStream() : null;
    }

    @Nullable
    @Override
    public OutputStream getOutputStream() {
        return process != null ? process.getOutputStream() : null;
    }

    public void stop() {
        if (process != null) {
            process.destroy();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BallerinaProcessStreamConnectionProvider) {
            BallerinaProcessStreamConnectionProvider other = (BallerinaProcessStreamConnectionProvider) obj;
            return commands.size() == other.commands.size()
                    && new HashSet<>(commands).equals(new HashSet<>(other.commands))
                    && workingDir.equals(other.workingDir);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(commands) ^ Objects.hashCode(workingDir);
    }
}
