package org.intellij.sdk.language;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BallerinaFileAction extends CreateFileFromTemplateAction {
    private static final String BALLERINA_TEMPLATE = "Ballerina_File";

    public BallerinaFileAction() {
        super("Ballerina File", "Create a new Ballerina file", BallerinaIcons.FILE);
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, @NotNull CreateFileFromTemplateDialog.Builder builder) {
        builder
                .setTitle("New Ballerina File")
                .addKind("Ballerina File", BallerinaIcons.FILE, BALLERINA_TEMPLATE)
                .setValidator(new InputValidator() {
                    @Override
                    public boolean checkInput(String inputString) {
                        return !inputString.isEmpty() && !inputString.contains(" ");
                    }

                    @Override
                    public boolean canClose(String inputString) {
                        // This method is invoked when the dialog is closing, allowing final validation.
                        // For simplicity, we are just reusing the `checkInput` logic.
                        return checkInput(inputString);
                    }
                });
        ;
    }

    @Override
    protected String getActionName(@NotNull PsiDirectory directory, @NotNull String newName, String templateName) {
        return "Create Ballerina File " + newName;
    }

    @Override
    protected String getDefaultTemplateProperty() {
        // Specify the default template used by this action
        return BALLERINA_TEMPLATE;
    }

    // Optionally, override getErrorTitle() to provide a custom error dialog title if needed
    @Override
    protected @NlsContexts.DialogTitle @NotNull String getErrorTitle() {
        return "Error Creating Ballerina File";
    }
}
