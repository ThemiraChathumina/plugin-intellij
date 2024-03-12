package org.intellij.sdk.language;

import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;
import com.intellij.openapi.components.StoredPropertyBase;
import kotlin.properties.PropertyDelegateProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BallerinaTestConfigurationOptions extends RunConfigurationOptions {

    private final StoredProperty<String> myScriptName =
            string("").provideDelegate(this, "scriptName");

    // Define a new StoredProperty for additional commands
    private final StoredProperty<String> myAdditionalCommands =
            string("").provideDelegate(this, "additionalCommands");

    public String getScriptName() {
        return myScriptName.getValue(this);
    }

    public void setScriptName(String scriptName) {
        myScriptName.setValue(this, scriptName);
    }

    // Getter for additional commands
    public String getAdditionalCommands() {
        return myAdditionalCommands.getValue(this);
    }

    // Setter for additional commands
    public void setAdditionalCommands(String additionalCommands) {
        myAdditionalCommands.setValue(this, additionalCommands);
    }
}
