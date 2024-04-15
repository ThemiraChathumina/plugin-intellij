package io.ballerina.plugins.idea.runconfig.test;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.NotNullLazyValue;
import io.ballerina.plugins.idea.BallerinaIcons;

import javax.swing.Icon;

final class BallerinaTestConfigurationType extends ConfigurationTypeBase {

    static final String ID = "BallerinaTestConfiguration";

    BallerinaTestConfigurationType() {
        super(ID, "Ballerina Test", "Ballerina Test configuration type",
                NotNullLazyValue.createValue(() -> AllIcons.Nodes.Console));
        addFactory(new BallerinaTestConfigurationFactory(this));
    }

    @Override
    public Icon getIcon() {
        return BallerinaIcons.FILE;
    }

}
