package io.ballerina.plugins.idea.runconfig.application;

import com.intellij.execution.ExecutionException;
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
import io.ballerina.plugins.idea.runconfig.BallerinaExecutionConfiguration;
import io.ballerina.plugins.idea.sdk.BallerinaSdkService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BallerinaApplicationRunConfiguration extends BallerinaExecutionConfiguration {

    protected BallerinaApplicationRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @Override
    public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new BallerinaApplicationRunSettingsEditor(project);
    }

    @Override
    public @Nullable RunProfileState getState(@NotNull Executor executor,
                                              @NotNull ExecutionEnvironment executionEnvironment)
            throws ExecutionException {
        if (BallerinaSdkService.getInstance().getBallerinaVersion(executionEnvironment.getProject()) == null) {
            BallerinaNotification.notifyBallerinaNotDetected(executionEnvironment.getProject());
            return null;
        }

        return new BallerinaApplicationRunningState(executionEnvironment,
                BallerinaSdkService.getInstance().getBallerinaPath(project), getOptions().getScriptName(),
                List.of(getOptions().getAdditionalCommands().split(" ")));
    }
}
