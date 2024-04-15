package io.ballerina.plugins.idea.runconfig;

import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;

public class BallerinaExecutionConfigOptions extends RunConfigurationOptions {

    private final StoredProperty<String> myScriptName =
            string("").provideDelegate(this, "scriptName");

    private final StoredProperty<String> myAdditionalCommands =
            string("").provideDelegate(this, "additionalCommands");

    public String getScriptName() {
        return myScriptName.getValue(this);
    }

    public void setScriptName(String scriptName) {
        myScriptName.setValue(this, scriptName);
    }

    public String getAdditionalCommands() {
        return myAdditionalCommands.getValue(this);
    }

    public void setAdditionalCommands(String additionalCommands) {
        myAdditionalCommands.setValue(this, additionalCommands);
    }
}
