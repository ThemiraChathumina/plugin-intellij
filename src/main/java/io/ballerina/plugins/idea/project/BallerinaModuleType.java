package io.ballerina.plugins.idea.project;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import io.ballerina.plugins.idea.BallerinaIcons;
import io.ballerina.plugins.idea.sdk.BallerinaSdkDetection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep,
                                               @NotNull final ModuleBuilder moduleBuilder) {
        return null;
    }

    @Nullable
    @Override
    public ModuleWizardStep modifyProjectTypeStep(@NotNull SettingsStep settingsStep,
                                                  @NotNull final ModuleBuilder moduleBuilder) {
//        JComboBox sdkComboBox = new JComboBox(); // Populate this with available SDKs
//        List<BallerinaSdkDetection.MiniSdk> sdkList = BallerinaSdkDetection.getSdks();
//        settingsStep.addSettingsField("SDK:", sdkComboBox);
//        for (BallerinaSdkDetection.MiniSdk sdk : sdkList) {
//            sdkComboBox.addItem(sdk.getVersion());
//        }
//        if (!sdkList.isEmpty()) {
//            sdkComboBox.setSelectedIndex(0);  // Set the default selection to the first item
//            String defaultSdkVersion = sdkList.get(0).getVersion();
//            BallerinaModuleBuilder ballerinaModuleBuilder = (BallerinaModuleBuilder) moduleBuilder;
//            ballerinaModuleBuilder.setSdkVersion(defaultSdkVersion);
//            ballerinaModuleBuilder.setSdkPath(BallerinaSdkDetection.getSdkPath(defaultSdkVersion));
//        }
//        sdkComboBox.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String selectedSdkVersion = (String) sdkComboBox.getSelectedItem();
//                System.out.println("selected item is " + selectedSdkVersion);
//                BallerinaModuleBuilder ballerinaModuleBuilder = (BallerinaModuleBuilder) moduleBuilder;
//                ballerinaModuleBuilder.setSdkVersion(selectedSdkVersion);
//                ballerinaModuleBuilder.setSdkPath(BallerinaSdkDetection.getSdkPath(selectedSdkVersion));
//            }
//        });
        return null;
    }

}
