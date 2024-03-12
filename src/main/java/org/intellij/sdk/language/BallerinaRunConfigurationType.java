package org.intellij.sdk.language;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.NotNullLazyValue;

import javax.swing.*;

final class BallerinaRunConfigurationType extends ConfigurationTypeBase {

    static final String ID = "BallerinaRunConfiguration";

    BallerinaRunConfigurationType() {
        super(ID, "Ballerina", "Ballerina run configuration type",
                NotNullLazyValue.createValue(() -> AllIcons.Nodes.Console));
        addFactory(new BallerinaConfigurationFactory(this));
    }

    @Override
    public Icon getIcon() {
        return BallerinaIcons.FILE;
    }

}