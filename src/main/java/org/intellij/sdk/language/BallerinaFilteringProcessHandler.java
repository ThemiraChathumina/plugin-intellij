package org.intellij.sdk.language;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

public class BallerinaFilteringProcessHandler extends OSProcessHandler {
    private boolean compilationStarted = false;

    public BallerinaFilteringProcessHandler(GeneralCommandLine commandLine) throws ExecutionException {
        super(commandLine);
    }

    @Override
    public void notifyTextAvailable(@NotNull String text, @NotNull Key outputType) {
        if (!compilationStarted && text.contains("Compiling source")) {
            compilationStarted = true;
        }
        if (compilationStarted) {
            super.notifyTextAvailable(text, outputType);
        }
    }
}