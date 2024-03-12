package org.intellij.sdk.language;

import com.intellij.ide.util.projectWizard.WebTemplateNewProjectWizard;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ide.wizard.GeneratorNewProjectWizardBuilderAdapter;
import com.intellij.ide.wizard.NewProjectWizardStep;
import org.jetbrains.annotations.NotNull;

public class BallerinaProjectModuleBuilder extends GeneratorNewProjectWizardBuilderAdapter {
    public BallerinaProjectModuleBuilder() {
        super(new WebTemplateNewProjectWizard(new BallerinaProjectGenerator()));
    }
}
