package io.ballerina.plugins.idea.project;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;

public class BallerinaSettingsConfigurable implements Configurable {
    private JPanel mySettingsPanel;
    private JTextField mySettingField;

    @Override
    public String getDisplayName() {
        return "Ballerina";
    }

    @Override
    public JComponent createComponent() {
        mySettingsPanel = new JPanel();
        mySettingsPanel.add(new JLabel("Setting:"));
        mySettingField = new JTextField(20);
        mySettingsPanel.add(mySettingField);
        return mySettingsPanel;
    }

    @Override
    public boolean isModified() {
        // Implement logic to check if settings have been modified
        return false;
    }

    @Override
    public void apply() {
        // Implement logic to store modified settings
    }

    @Override
    public void reset() {
        // Implement logic to reset settings to their defaults
    }

    @Override
    public void disposeUIResources() {
        mySettingsPanel = null;
    }
}
