package io.ballerina.plugins.idea.project;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import javax.swing.*;

public class BallerinaModuleConfigurable implements Configurable {
    private JTextField sdkPathField;
    private Module module;
    private boolean isModified = false;

    public BallerinaModuleConfigurable(Module module) {
        this.module = module;
    }

    @Override
    public String getDisplayName() {
        return "Ballerina";
    }

    @Override
    public JComponent createComponent() {
        JPanel panel = new JPanel();
        sdkPathField = new JTextField(30);

        // Assume getBallerinaSdkPath is a method that retrieves the SDK path for the module
        sdkPathField.setText(getBallerinaSdkPath(module));
        panel.add(new JLabel("SDK Path:"));
        panel.add(sdkPathField);
        return panel;
    }

    @Override
    public boolean isModified() {
        // Implement logic to determine if settings have been modified
        return isModified;
    }

    @Override
    public void apply() throws ConfigurationException {
        // Implement logic to apply changes when the user hits OK or Apply
        // You will also set the SDK path to the module settings here
        isModified = false;
    }

    @Override
    public void reset() {
        // Implement logic to reset to initial values
    }

    @Override
    public void disposeUIResources() {
        // Clean up resources if necessary
    }

    private String getBallerinaSdkPath(Module module) {
        // Implement the logic to retrieve the Ballerina SDK path from module settings
        return "Path to Ballerina SDK";
    }
}
