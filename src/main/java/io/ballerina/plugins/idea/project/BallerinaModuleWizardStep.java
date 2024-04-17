package io.ballerina.plugins.idea.project;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.IconUtil;
import io.ballerina.plugins.idea.BallerinaIcons;
import io.ballerina.plugins.idea.sdk.BallerinaSdkDetection;

import java.awt.*;
import java.util.List;

import javax.swing.*;

public class BallerinaModuleWizardStep extends ModuleWizardStep {

    private final JComboBox<String> sdkComboBox = new JComboBox<>();
    private final BallerinaModuleBuilder moduleBuilder;
    private final JLabel titleLabel = new JLabel("Select Ballerina SDK");

    public BallerinaModuleWizardStep(BallerinaModuleBuilder moduleBuilder) {
        this.moduleBuilder = moduleBuilder;
        initSdkComboBox();
    }

    private void initSdkComboBox() {
        List<BallerinaSdkDetection.MiniSdk> sdkList = BallerinaSdkDetection.getSdks();
        for (BallerinaSdkDetection.MiniSdk sdk : sdkList) {
            sdkComboBox.addItem(sdk.getVersion());
        }
        if (!sdkList.isEmpty()) {
            sdkComboBox.setSelectedIndex(0);  // Set the default selection to the first item
        }
        sdkComboBox.addActionListener(e -> {
            String selectedSdkVersion = (String) sdkComboBox.getSelectedItem();
            if (selectedSdkVersion != null) {
                moduleBuilder.setSdkVersion(selectedSdkVersion);
                moduleBuilder.setSdkPath(BallerinaSdkDetection.getSdkPath(selectedSdkVersion));
            }
        });
    }
    private Icon resizeIcon(Icon icon, int width, int height) {
        return IconUtil.scale(icon, null, (float)width / icon.getIconWidth());
    }

    @Override
    public JComponent getComponent() {
        // Assuming BallerinaIcons.HEADLINE is the icon you want to resize
        Icon originalIcon = BallerinaIcons.HEADLINE;
        Icon resizedIcon = resizeIcon(originalIcon, 256, 256); // Desired size
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Common constraints for all components
        gbc.gridx = 0; // Align to the first column
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH; // Anchor components to the top
        gbc.weightx = 1; // Distribute extra horizontal space
        gbc.insets = new Insets(1, 10, 1, 10); // Add some padding

        // Icon constraints
        gbc.gridy = 0; // First row for the icon
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Component takes up the entire row
        gbc.weighty = 1; // Give vertical space, so the icon stays on top when window is resized
        JLabel iconLabel = new JLabel(resizedIcon);
        panel.add(iconLabel, gbc);

        // Title constraints
        gbc.gridy = 1; // Next row for the title label
        gbc.gridwidth = 1; // Reset to default
        gbc.weighty = 0; // Reset to default
        panel.add(titleLabel, gbc);

        // Combo box constraints
        gbc.gridy = 2; // Next row for the combo box
        panel.add(sdkComboBox, gbc);

        // Fill the remaining space
        gbc.gridy = 3; // The row after the last component
        gbc.weighty = 16; // Give weight to push components to the top
        panel.add(Box.createGlue(), gbc); // Invisible component that takes up space

        return panel;
    }
    @Override
    public void updateDataModel() {
        // Update the module builder with the selected SDK version
        String selectedSdkVersion = (String) sdkComboBox.getSelectedItem();
        if (selectedSdkVersion != null) {
            moduleBuilder.setSdkVersion(selectedSdkVersion);
            moduleBuilder.setSdkPath(BallerinaSdkDetection.getSdkPath(selectedSdkVersion));
        }
    }
}

