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
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BallerinaTestConfiguration extends RunConfigurationBase<BallerinaTestConfigurationOptions> {


    protected BallerinaTestConfiguration(Project project,
                                        ConfigurationFactory factory,
                                        String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    protected BallerinaTestConfigurationOptions getOptions() {
        return (BallerinaTestConfigurationOptions) super.getOptions();
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();

        String ballerinaVersion = BallerinaSdkService.getInstance().getBallerinaVersion();

        if (ballerinaVersion == null) {
            throw new RuntimeConfigurationException("Ballerina SDK is not detected.");
        }
    }


    public void addCommand(String cmd){
        getOptions().setAdditionalCommands(cmd);
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
        return new BallerinaTestSettingsEditor();
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor,
                                    @NotNull ExecutionEnvironment environment) {
        if (BallerinaSdkService.getInstance().getBallerinaVersion() == null) {
            BallerinaNotification.notifyBallerinaNotDetected(environment.getProject());
            return null;
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

                GeneralCommandLine commandLine =
                        new GeneralCommandLine(BalPath,"test");

                for (String cmd : getOptions().getAdditionalCommands().split(" ")) {
                    commandLine.addParameter(cmd);
                }

                commandLine.setWorkDirectory(script);
                OSProcessHandler processHandler = ProcessHandlerFactory.getInstance()
                        .createColoredProcessHandler(commandLine);
                ProcessTerminatedListener.attach(processHandler);
                return processHandler;

            }
        };
    }



}