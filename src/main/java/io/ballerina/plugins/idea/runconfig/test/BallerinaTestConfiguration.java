package io.ballerina.plugins.idea.runconfig.test;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import io.ballerina.plugins.idea.notification.BallerinaNotification;
import io.ballerina.plugins.idea.runconfig.BallerinaExecutionConfigOptions;
import io.ballerina.plugins.idea.sdk.BallerinaSdkService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BallerinaTestConfiguration extends RunConfigurationBase<BallerinaExecutionConfigOptions> {

    protected BallerinaTestConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    protected BallerinaExecutionConfigOptions getOptions() {
        return (BallerinaExecutionConfigOptions) super.getOptions();
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();

        String ballerinaVersion = BallerinaSdkService.getInstance().getBallerinaVersion();

        if (ballerinaVersion == null) {
            throw new RuntimeConfigurationException("Ballerina SDK is not detected.");
        }
    }

    public void addCommand(String cmd) {
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
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        if (BallerinaSdkService.getInstance().getBallerinaVersion() == null) {
            BallerinaNotification.notifyBallerinaNotDetected(environment.getProject());
            return null;
        }

        return new BallerinaTestState(environment, BallerinaSdkService.getInstance().getBallerinaPath(),
                getOptions().getScriptName(), List.of(getOptions().getAdditionalCommands().split(" ")));
    }
}
