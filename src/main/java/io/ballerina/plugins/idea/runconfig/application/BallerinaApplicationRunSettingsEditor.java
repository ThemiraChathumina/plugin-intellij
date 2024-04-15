package io.ballerina.plugins.idea.runconfig.application;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.FormBuilder;
import io.ballerina.plugins.idea.sdk.BallerinaSdkService;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BallerinaApplicationRunSettingsEditor extends SettingsEditor<BallerinaApplicationRunConfiguration> {

    private final JPanel myPanel;
    private final TextFieldWithBrowseButton scriptPathField;
    private final JTextField ballerinaSdkField; // Changed to JTextField

    public BallerinaApplicationRunSettingsEditor() {
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

        formbuilder.addLabeledComponent("Ballerina SDK", ballerinaSdkField);

        myPanel = formbuilder.getPanel();
    }

    @Override
    protected void resetEditorFrom(BallerinaApplicationRunConfiguration demoRunConfiguration) {
        scriptPathField.setText(demoRunConfiguration.getScriptName());
    }

    @Override
    protected void applyEditorTo(@NotNull BallerinaApplicationRunConfiguration demoRunConfiguration) {
        demoRunConfiguration.setScriptName(scriptPathField.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myPanel;
    }
}
