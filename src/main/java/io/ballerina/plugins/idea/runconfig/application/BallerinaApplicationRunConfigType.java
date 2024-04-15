package io.ballerina.plugins.idea.runconfig.application;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.NotNullLazyValue;
import io.ballerina.plugins.idea.BallerinaIcons;

import javax.swing.Icon;

final class BallerinaApplicationRunConfigType extends ConfigurationTypeBase {

    static final String ID = "BallerinaRunConfiguration";

    BallerinaApplicationRunConfigType() {
        super(ID, "Ballerina", "Ballerina run configuration type",
                NotNullLazyValue.createValue(() -> AllIcons.Nodes.Console));
        addFactory(new BallerinaApplicationRunConfigurationFactory(this));
    }

    @Override
    public Icon getIcon() {
        return BallerinaIcons.FILE;
    }

}
