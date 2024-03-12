package org.intellij.sdk.language;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BallerinaSettingsEditor extends SettingsEditor<BallerinaRunConfiguration> {

    private final JPanel myPanel;
    private final TextFieldWithBrowseButton scriptPathField;
    private final JTextField ballerinaSdkField; // Changed to JTextField

    public BallerinaSettingsEditor() {
        scriptPathField = new TextFieldWithBrowseButton();
        scriptPathField.addBrowseFolderListener("Select Run Source", null, null,
                FileChooserDescriptorFactory.createSingleFileDescriptor());

        String balVersion = BallerinaSdkService.getInstance().getBallerinaVersion();
        ballerinaSdkField = new JTextField(); // Initialize as JTextField
        if (balVersion != null) {
            ballerinaSdkField.setText(balVersion);
            ballerinaSdkField.setEditable(false); // Make the text field non-editable
        }

        FormBuilder formbuilder = FormBuilder.createFormBuilder()
                .addLabeledComponent("Run source", scriptPathField);

        // Add the Ballerina SDK version field regardless of whether balVersion is null, since it's now a fixed text field
        formbuilder.addLabeledComponent("Ballerina SDK", ballerinaSdkField);

        myPanel = formbuilder.getPanel();
    }

    @Override
    protected void resetEditorFrom(BallerinaRunConfiguration demoRunConfiguration) {
        scriptPathField.setText(demoRunConfiguration.getScriptName());
    }

    @Override
    protected void applyEditorTo(@NotNull BallerinaRunConfiguration demoRunConfiguration) {
        demoRunConfiguration.setScriptName(scriptPathField.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myPanel;
    }
}
