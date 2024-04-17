package io.ballerina.plugins.idea.runconfig.test;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.FormBuilder;
import io.ballerina.plugins.idea.sdk.BallerinaSdkService;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BallerinaTestSettingsEditor extends SettingsEditor<BallerinaTestConfiguration> {

    private final JPanel myPanel;
    private final TextFieldWithBrowseButton scriptPathField;
    private final JTextField ballerinaSdkField;

    public BallerinaTestSettingsEditor(Project project) {
        scriptPathField = new TextFieldWithBrowseButton();
        scriptPathField.addBrowseFolderListener("Select Test Source", null, null,
                FileChooserDescriptorFactory.createSingleFileDescriptor());

        String balVersion = BallerinaSdkService.getInstance().getBallerinaVersion(project);
        ballerinaSdkField = new JTextField();
        if (balVersion != null) {
            ballerinaSdkField.setText(balVersion);
            ballerinaSdkField.setEditable(false);
        }

        FormBuilder formbuilder = FormBuilder.createFormBuilder().addLabeledComponent("Test source", scriptPathField);

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
