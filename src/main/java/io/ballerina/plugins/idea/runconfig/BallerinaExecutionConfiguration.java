package io.ballerina.plugins.idea.runconfig;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.project.Project;
import io.ballerina.plugins.idea.sdk.BallerinaSdkService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BallerinaExecutionConfiguration extends RunConfigurationBase<BallerinaExecutionConfigOptions> {

    protected final Project project;

    protected BallerinaExecutionConfiguration(@NotNull Project project,
                                              @Nullable ConfigurationFactory factory,
                                              @Nullable String name) {
        super(project, factory, name);
        this.project = project;
    }

    @NotNull
    @Override
    protected BallerinaExecutionConfigOptions getOptions() {
        return (BallerinaExecutionConfigOptions) super.getOptions();
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();

        String ballerinaVersion = BallerinaSdkService.getInstance().getBallerinaVersion(project);

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

}
