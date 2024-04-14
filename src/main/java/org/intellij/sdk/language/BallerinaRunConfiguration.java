package org.intellij.sdk.language;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.history.core.Paths;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BallerinaRunConfiguration extends RunConfigurationBase<BallerinaRunConfigurationOptions> {

    protected BallerinaRunConfiguration(Project project,
                                        ConfigurationFactory factory,
                                        String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    protected BallerinaRunConfigurationOptions getOptions() {
        return (BallerinaRunConfigurationOptions) super.getOptions();
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();

        // Get the Ballerina version
        String ballerinaVersion = BallerinaSdkService.getInstance().getBallerinaVersion();

        // If the version is null, throw a RuntimeConfigurationError
        if (ballerinaVersion == null) {
            throw new RuntimeConfigurationException("Ballerina SDK is not detected.");
        }
    }




    public String getScriptName() {
        return getOptions().getScriptName();
    }

    public void setScriptName(String scriptName) {
        getOptions().setScriptName(scriptName);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new BallerinaSettingsEditor();
    }

    public static String getLastFolderOrFile(String path) {
        if (path == null || path.isEmpty()) {
            return null; // or throw an IllegalArgumentException
        }

        // Normalize the path to remove redundant slashes
        String normalizedPath = path.replaceAll("[/\\\\]+", "/");

        // Remove trailing slashes if present
        normalizedPath = normalizedPath.replaceAll("/$", "");

        // Split the path by slashes and get the last part
        String[] parts = normalizedPath.split("/");
        return parts.length > 0 ? parts[parts.length - 1] : null;
    }

    public static String getParentPath(String path) {
        if (path == null || path.isEmpty()) {
            return null; // or throw an IllegalArgumentException
        }

        // Normalize the path to remove redundant slashes
        String normalizedPath = path.replaceAll("[/\\\\]+", "/");

        // Remove trailing slashes if present
        normalizedPath = normalizedPath.replaceAll("/$", "");

        // Find the last index of the separator
        int lastSeparatorIndex = normalizedPath.lastIndexOf('/');

        // If there's no separator or only at the beginning (for Unix-based systems), return null or root
        if (lastSeparatorIndex <= 0) {
            return null; // or return "/";
        }

        // Return the substring from the beginning to the last separator
        return normalizedPath.substring(0, lastSeparatorIndex);
    }


    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor,
                                    @NotNull ExecutionEnvironment environment) {
        if (BallerinaSdkService.getInstance().getBallerinaVersion() == null) {
            BallerinaNotification.notifyBallerinaNotDetected(environment.getProject());
            return null; // Return null to indicate non-executable state
        }
        return new CommandLineState(environment) {
            @NotNull
            @Override
            protected ProcessHandler startProcess() throws ExecutionException {
                String BalPath = BallerinaSdkService.getInstance().getBallerinaPath();
                String script =  getOptions().getScriptName();
                String ballerinaPackage = BallerinaSdkDetection.findBallerinaPackage(script);
                if (!ballerinaPackage.isEmpty()) {
                    script = ballerinaPackage;
                }

                String lastPath = getLastFolderOrFile(script);
                System.out.println("Last Path: " + lastPath);
                String parentPath = getParentPath(script);
                System.out.println("Parent Path: " + parentPath);

//                GeneralCommandLine commandLine =
//                        new GeneralCommandLine(BalPath,"run","--debug","8081",lastPath);
                GeneralCommandLine commandLine =
                        new GeneralCommandLine(BalPath,"run",lastPath);
                commandLine.setWorkDirectory(parentPath);
                OSProcessHandler processHandler = ProcessHandlerFactory.getInstance()
                        .createColoredProcessHandler(commandLine);
//                BallerinaFilteringProcessHandler processHandler = new BallerinaFilteringProcessHandler(commandLine);

                ProcessTerminatedListener.attach(processHandler);
                return processHandler;

            }
        };
    }



}