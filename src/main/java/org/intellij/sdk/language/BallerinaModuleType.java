package org.intellij.sdk.language;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

final class BallerinaModuleType extends ModuleType<BallerinaModuleBuilder> {

    private static final String ID = "BALLERINA_MODULE_TYPE";

    BallerinaModuleType() {
        super(ID);
    }

    public static BallerinaModuleType getInstance() {
        return (BallerinaModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public BallerinaModuleBuilder createModuleBuilder() {
        return new BallerinaModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "Ballerina Package";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Example custom module type";
    }

    @NotNull
    @Override
    public Icon getNodeIcon(@Deprecated boolean b) {
        return BallerinaIcons.FILE;
    }

    @Override
    public ModuleWizardStep @NotNull [] createWizardSteps(@NotNull WizardContext wizardContext,
                                                          @NotNull BallerinaModuleBuilder moduleBuilder,
                                                          @NotNull ModulesProvider modulesProvider) {
        return super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider);
    }

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep, @NotNull final ModuleBuilder moduleBuilder) {
        if (!(moduleBuilder instanceof BallerinaModuleBuilder)) {
            return null;
        }

        // Add custom settings components to the settings step
        // For example, add a SDK selection combo box
        JComboBox sdkComboBox = new JComboBox(); // Populate this with available SDKs
        settingsStep.addSettingsField("SDK:", sdkComboBox);

        // You can add more settings as needed

        // Return null if you don't need to add an additional step after the settings step
        return null;
    }


    @Nullable
    @Override
    public ModuleWizardStep modifyProjectTypeStep(@NotNull SettingsStep settingsStep, @NotNull final ModuleBuilder moduleBuilder) {
        JComboBox sdkComboBox = new JComboBox(); // Populate this with available SDKs
        settingsStep.addSettingsField("SDK:", sdkComboBox);
        return null;
    }

}