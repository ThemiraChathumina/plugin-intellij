package io.ballerina.plugins.idea.runconfig.test;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import io.ballerina.plugins.idea.notification.BallerinaNotification;
import io.ballerina.plugins.idea.runconfig.BallerinaExecutionConfiguration;
import io.ballerina.plugins.idea.sdk.BallerinaSdkService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BallerinaTestConfiguration extends BallerinaExecutionConfiguration {

    protected BallerinaTestConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new BallerinaTestSettingsEditor(project);
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        if (BallerinaSdkService.getInstance().getBallerinaVersion(environment.getProject()) == null) {
            BallerinaNotification.notifyBallerinaNotDetected(environment.getProject());
            return null;
        }

        return new BallerinaTestState(environment,
                BallerinaSdkService.getInstance().getBallerinaPath(environment.getProject()),
                getOptions().getScriptName(), List.of(getOptions().getAdditionalCommands().split(" ")));
    }
}
