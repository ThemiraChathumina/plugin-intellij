package org.intellij.sdk.language;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BallerinaTestSettingsEditor extends SettingsEditor<BallerinaTestConfiguration> {

    private final JPanel myPanel;
    private final TextFieldWithBrowseButton scriptPathField;
    private final JTextField ballerinaSdkField; // Changed to JTextField

    public BallerinaTestSettingsEditor() {
        scriptPathField = new TextFieldWithBrowseButton();
        scriptPathField.addBrowseFolderListener("Select Test Source", null, null,
                FileChooserDescriptorFactory.createSingleFileDescriptor());

        String balVersion = BallerinaSdkService.getInstance().getBallerinaVersion();
        ballerinaSdkField = new JTextField(); // Initialize as JTextField
        if (balVersion != null) {
            ballerinaSdkField.setText(balVersion);
            ballerinaSdkField.setEditable(false); // Make the text field non-editable
        }

        FormBuilder formbuilder = FormBuilder.createFormBuilder()
                .addLabeledComponent("Test source", scriptPathField);

        // Add the Ballerina SDK version field regardless of whether balVersion is null, since it's now a fixed text field
        formbuilder.addLabeledComponent("Ballerina SDK", ballerinaSdkField);

        myPanel = formbuilder.getPanel();
    }

    @Override
    protected void resetEditorFrom(BallerinaTestConfiguration demoTestConfiguration) {
        scriptPathField.setText(demoTestConfiguration.getScriptName());
    }

    @Override
    protected void applyEditorTo(@NotNull BallerinaTestConfiguration demoTestConfiguration) {
        demoTestConfiguration.setScriptName(scriptPathField.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myPanel;
    }
}
