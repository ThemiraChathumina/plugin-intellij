package org.intellij.sdk.language;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.SdkListCellRenderer;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.ComboBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

//public class BallerinaModuleWizardStep extends ModuleWizardStep {
//
//    private ComboBox<Sdk> sdkComboBox;
////    private final ProjectSdksModel sdksModel;
//
//
//
//    @Override
//    public JComponent getComponent() {
//        JPanel panel = new JPanel(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.gridwidth = 2; // Span two columns.
//        gbc.anchor = GridBagConstraints.WEST;
//        gbc.insets = new Insets(5, 15, 5, 5); // Top, left, bottom, right margins
//        panel.add(new JLabel("Project SDK:"), gbc);
//
//        sdkComboBox = new ComboBox<>();
////        String sdkName = BallerinaSdkDetection.getBallerinaVersion();
//        String sdkName = BallerinaSdkService.getInstance().getBallerinaVersion();
//
//        sdkComboBox.setRenderer(new SdkListCellRenderer(sdkName));
//        // resetSdkComboBox();  // Populate the SDK combo box
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        gbc.weightx = 1.0; // This will allow the combo box to expand horizontally
//        gbc.fill = GridBagConstraints.HORIZONTAL; // This will make the combo box fill its grid horizontally
//        // Keep the same insets to maintain consistency
//        gbc.insets = new Insets(5, 15, 5, 15); // Same margin for the combo box
//
//        panel.add(sdkComboBox, gbc);
//
//        return panel;
//    }
//
//
////    private void resetSdkComboBox() {
////        Sdk @NotNull [] sdkList = sdksModel.getSdks();
////        for (Sdk sdk : sdkList) {
////            sdkComboBox.addItem(sdk);
////        }
////    }
//
//    @Override
//    public void updateDataModel() {
//        //todo update model according to UI
//    }
//
//}

public class BallerinaModuleWizardStep extends ModuleWizardStep {

    private final ModuleBuilder moduleBuilder;
    private ComboBox<Sdk> sdkComboBox;
    private JTextField moduleNameField; // Field for module name input
    private JTextField modulePathField; // Field for module path input

    public BallerinaModuleWizardStep(ModuleBuilder moduleBuilder) {
        this.moduleBuilder = moduleBuilder;
    }

    @Override
    public JComponent getComponent() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4); // Margins for all components
        gbc.anchor = GridBagConstraints.WEST;

        // SDK Selection Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Project SDK:"), gbc);

        // SDK Selection ComboBox
        sdkComboBox = new ComboBox<>();
        gbc.gridx = 1;
        panel.add(sdkComboBox, gbc);

        // Module Name Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Module Name:"), gbc);

        // Module Name TextField
        moduleNameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(moduleNameField, gbc);

        // Module Path Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Module Path:"), gbc);

        // Module Path TextField
        modulePathField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(modulePathField, gbc);

        return panel;
    }

    @Override
    public void updateDataModel() {
        // Get the module builder and set the selected SDK, module name, and module path
        BallerinaModuleBuilder builder = (BallerinaModuleBuilder) getModuleBuilder();
        if (builder != null) {
            // Set the SDK
            Sdk selectedSdk = sdkComboBox.getItem();
//            builder.setModuleSdk(selectedSdk);

            // Apply the module name and path
            String moduleName = moduleNameField.getText().trim();
            String modulePath = modulePathField.getText().trim();
            builder.setName(moduleName); // Set the module name
            builder.setContentEntryPath(modulePath); // Set the content root for the module
        }
    }

    private ModuleBuilder getModuleBuilder() {
        // Implement logic to retrieve the current module builder instance, if required
        return this.moduleBuilder;
    }
}
