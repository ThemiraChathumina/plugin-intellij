package io.ballerina.plugins.idea.runconfig.application;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;
import io.ballerina.plugins.idea.runconfig.BallerinaExecutionConfigOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BallerinaApplicationRunConfigurationFactory extends ConfigurationFactory {

    protected BallerinaApplicationRunConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @Override
    public @NotNull String getId() {
        return BallerinaApplicationRunConfigType.ID;
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(
            @NotNull Project project) {
        return new BallerinaApplicationRunConfiguration(project, this, "Ballerina");
    }

    @Nullable
    @Override
    public Class<? extends BaseState> getOptionsClass() {
        return BallerinaExecutionConfigOptions.class;
    }

}
