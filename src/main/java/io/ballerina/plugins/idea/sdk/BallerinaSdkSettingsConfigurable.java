package io.ballerina.plugins.idea.sdk;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import io.ballerina.plugins.idea.notification.BallerinaNotification;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BallerinaSdkSettingsConfigurable implements Configurable {

    private final Project project;
    private ComboBox<String> sdkVersionComboBox;
    private JLabel selectedVersionTextField;
    private String selectedSdkPath = "";
    private List<BallerinaSdkUtil.BallerinaSdk> sdkList;
    private JCheckBox useCustomSdkCheckbox;
    private boolean modified = false;

    public BallerinaSdkSettingsConfigurable(Project project) {
        this.project = project;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Ballerina";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        sdkVersionComboBox = new ComboBox<>();
        selectedVersionTextField = new JLabel();
        useCustomSdkCheckbox = new JCheckBox("Use custom Ballerina SDK");
        useCustomSdkCheckbox.setSelected(false);
        Color defaultColor = selectedVersionTextField.getForeground();
        sdkList = BallerinaSdkService.getInstance().getSdkList();
        BallerinaSdkSettings.getInstance().setBallerinaSdkPath("");
        BallerinaSdkSettings.getInstance().setBallerinaSdkVersion("");
        String systemBalPath = BallerinaSdkService.getInstance().getSystemBalPath();
        String systemBalVersion = BallerinaSdkService.getInstance().getSystemBalVersion();
        if (!useCustomSdkCheckbox.isSelected()) {
            if (BallerinaSdkUtil.isValidSdk(systemBalPath, systemBalVersion)) {
                sdkVersionComboBox.addItem("System default: " + systemBalPath);
                selectedVersionTextField.setText("System Ballerina version: " + systemBalVersion);
                selectedSdkPath = systemBalPath;
            } else {
                selectedVersionTextField.setText("No valid Ballerina SDK found");
                selectedVersionTextField.setForeground(JBColor.RED);
            }
        } else {
            String savedPath = BallerinaSdkSettings.getInstance().getBallerinaSdkPath();
            String savedVersion = BallerinaSdkSettings.getInstance().getBallerinaSdkVersion();
            if (BallerinaSdkUtil.isValidSdk(systemBalPath, systemBalVersion)) {
                sdkVersionComboBox.addItem("System default: " + systemBalPath);
            }
            if (BallerinaSdkUtil.isValidSdk(savedPath, savedVersion)) {
                sdkVersionComboBox.addItem(savedPath);
                sdkVersionComboBox.setSelectedItem(savedPath);
                selectedSdkPath = savedPath;
            } else {
                if (BallerinaSdkUtil.isValidSdk(systemBalPath, systemBalVersion)) {
                    sdkVersionComboBox.setSelectedIndex(0);
                    selectedSdkPath = systemBalPath;
                } else {
                    selectedVersionTextField.setText("No valid Ballerina SDK found");
                    selectedVersionTextField.setForeground(JBColor.RED);
                }
            }
        }
        for (BallerinaSdkUtil.BallerinaSdk sdk : sdkList) {
            if (!Objects.equals(selectedSdkPath, sdk.getPath())
            && !Objects.equals(systemBalPath, sdk.getPath())) {
                sdkVersionComboBox.addItem(sdk.getPath());
            }
        }
        sdkVersionComboBox.addItem("Add Ballerina SDK");
        sdkVersionComboBox.setEnabled(useCustomSdkCheckbox.isSelected());
        useCustomSdkCheckbox.addActionListener(e -> {
            boolean selected = useCustomSdkCheckbox.isSelected();
            if (!selected) {
                String path = sdkVersionComboBox.getItemAt(0);
                if (Objects.equals(path, "System default: " + systemBalPath)) {
                    sdkVersionComboBox.setSelectedIndex(0);
                } else {
                    sdkVersionComboBox.setSelectedItem("Add Ballerina SDK");
                    selectedVersionTextField.setText("No valid Ballerina SDK found");
                }
            }
            sdkVersionComboBox.setEnabled(selected);
        });
        sdkVersionComboBox.addActionListener(e -> {
            if ("Add Ballerina SDK".equals(sdkVersionComboBox.getSelectedItem())) {
                VirtualFile file = FileChooser.chooseFile(
                        new FileChooserDescriptor(true, false, false,
                                false, false, false),
                        project, null);
                if (file != null) {
                    String sdkPath = file.getPath();
                    if (BallerinaSdkUtil.isValidPath(sdkPath)) {
                        sdkPath = Paths.get(sdkPath).normalize().toString();
                        selectedSdkPath = sdkPath;
                        sdkVersionComboBox.insertItemAt(sdkPath, 1);
                        sdkVersionComboBox.setSelectedIndex(1);
                        selectedVersionTextField.setText(BallerinaSdkUtil.getVersionFromPath(sdkPath));
                    } else {
                        selectedVersionTextField.setText("Invalid Ballerina SDK path selected");
                        selectedVersionTextField.setForeground(JBColor.RED);
                    }
                }
                // Reset the selection to the prompt
                sdkVersionComboBox.setSelectedIndex(0);
            } else {
                String path = (String) sdkVersionComboBox.getSelectedItem();
                if (Objects.equals(path, "System default: " + systemBalPath)) {
                    selectedSdkPath = systemBalPath;
                    selectedVersionTextField.setText("System Ballerina version: " + systemBalVersion);
                    selectedVersionTextField.setForeground(defaultColor);
                } else {
                    String version = BallerinaSdkUtil.getVersionFromPath(path);
                    if (!version.isEmpty()) {
                        version = "Ballerina version: " + version;
                    }
                    selectedSdkPath = path;
                    selectedVersionTextField.setText(version);
                    selectedVersionTextField.setForeground(defaultColor);
                }
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel label = new JLabel("Ballerina SDK Path:");
        Font labelFont = label.getFont();
        label.setFont(new Font(labelFont.getName(), labelFont.getStyle(), labelFont.getSize() + 1));

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0;  // Do not allow vertical stretching
        gbc.insets = new Insets(2, 0, 2, 0);
        panel.add(label, gbc);

        gbc.gridy = 1;
        panel.add(sdkVersionComboBox, gbc);

        gbc.gridy = 2;
        panel.add(selectedVersionTextField, gbc);

        gbc.gridy = 3;
        panel.add(useCustomSdkCheckbox, gbc);

        // Add a vertical filler panel to push everything to the top
        JPanel filler = new JPanel();
        gbc.gridy = 4;
        gbc.weighty = 1;  // Take up all extra space
        panel.add(filler, gbc);

        return panel;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() {
        if (useCustomSdkCheckbox.isSelected()
                && !Objects.equals(BallerinaSdkSettings.getInstance().getBallerinaSdkPath(), selectedSdkPath)
                && !Objects.equals(selectedSdkPath, BallerinaSdkService.getInstance().getSystemBalPath())) {
            BallerinaSdkService.getInstance().setBallerinaSdk((String) sdkVersionComboBox.getSelectedItem(),
                    BallerinaSdkUtil.getVersionFromPath((String) sdkVersionComboBox.getSelectedItem()));
            modified = true;
            BallerinaSdkSettings.getInstance().setUseCustomSdk(useCustomSdkCheckbox.isSelected());
        }
    }

    @Override
    public void disposeUIResources() {
        sdkVersionComboBox = null;
        selectedVersionTextField = null;
        if (modified) {
            BallerinaNotification.notifyRestartIde(project);
        }
    }
}
