package org.intellij.sdk.language;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BallerinaConfigurationFactory extends ConfigurationFactory {

    protected BallerinaConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @Override
    public @NotNull String getId() {
        return BallerinaRunConfigurationType.ID;
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(
            @NotNull Project project) {
        return new BallerinaRunConfiguration(project, this, "Ballerina");
    }

    @Nullable
    @Override
    public Class<? extends BaseState> getOptionsClass() {
        return BallerinaRunConfigurationOptions.class;
    }

}