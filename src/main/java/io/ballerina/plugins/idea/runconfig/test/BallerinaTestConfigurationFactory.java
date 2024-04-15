package io.ballerina.plugins.idea.runconfig.test;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;
import io.ballerina.plugins.idea.runconfig.BallerinaExecutionConfigOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BallerinaTestConfigurationFactory extends ConfigurationFactory {

    protected BallerinaTestConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @Override
    public @NotNull String getId() {
        return BallerinaTestConfigurationType.ID;
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new BallerinaTestConfiguration(project, this, "Ballerina Test");
    }

    @Nullable
    @Override
    public Class<? extends BaseState> getOptionsClass() {
        return BallerinaExecutionConfigOptions.class;
    }

}
