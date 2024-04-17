package io.ballerina.plugins.idea.project;

import com.intellij.ide.util.projectWizard.WebTemplateNewProjectWizard;
import com.intellij.ide.wizard.GeneratorNewProjectWizardBuilderAdapter;

public class BallerinaProjectModuleBuilder extends GeneratorNewProjectWizardBuilderAdapter {

    public BallerinaProjectModuleBuilder() {
        super(new WebTemplateNewProjectWizard(new BallerinaProjectGenerator()));
    }
}