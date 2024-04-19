package io.ballerina.plugins.idea.configuration.ui;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import io.ballerina.plugins.idea.sdk.BallerinaSdkService;
import io.ballerina.plugins.idea.sdk.BallerinaSdkSettings;
import io.ballerina.plugins.idea.sdk.BallerinaSdkUtil;

import java.awt.Color;
import java.awt.Font;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

public class BallerinaSdkSelection {

    private ComboBox<String> sdkVersionComboBox = new ComboBox<>();
    private JLabel selectedVersionTextField = new JLabel();
    private JCheckBox useCustomSdkCheckbox = new JCheckBox();

    private JLabel label = new JLabel();

    private String selectedSdkPath = "";
    private String selectedSdkVersion = "";
    private BallerinaSdkService sdkService;
    private BallerinaSdkSettings sdkSettings;
    private Color defaultColor;
    private final Project project;
    private final String comboBoxDefaultStart = "System default: ";
    private final String addBallerinaSdk = "Add Ballerina SDK";
    private final String systemBallerinaVersionStart = "System Ballerina version: ";
    private final String ballerinaSwanLake = "Ballerina Swan Lake";
    private final String invalidBalSdkPath = "Invalid Ballerina SDK path selected";
    private final String noValidBalSdk = "No valid Ballerina SDK found";
    private final String labelText = "Ballerina SDK Path:";
    private final String checkboxText = "Use custom Ballerina SDK";

    public BallerinaSdkSelection(Project project) {
        this.project = project;
    }

    public void sdkSelection() {
        label.setText("Ballerina SDK Path:");
        Font labelFont = label.getFont();
        label.setFont(new Font(labelFont.getName(), labelFont.getStyle(), labelFont.getSize() + 1));
        useCustomSdkCheckbox.setText("Use custom Ballerina SDK");
        useCustomSdkCheckbox.setSelected(BallerinaSdkSettings.getInstance().isUseCustomSdk());
        Color defaultColor = selectedVersionTextField.getForeground();
        List<BallerinaSdkUtil.BallerinaSdk> sdkList = BallerinaSdkService.getInstance().getSdkList();
        String systemBalPath = BallerinaSdkService.getInstance().getSystemBalPath();
        String systemBalVersion = BallerinaSdkService.getInstance().getSystemBalVersion();
        if (!useCustomSdkCheckbox.isSelected()) {
            if (BallerinaSdkUtil.isValidSdk(systemBalPath, systemBalVersion)) {
                sdkVersionComboBox.addItem("System default: " + BallerinaSdkUtil.findBalDistFolder(systemBalPath));
                selectedVersionTextField.setText("System Ballerina version: " + systemBalVersion);
                selectedSdkPath = systemBalPath;
                selectedSdkVersion = systemBalVersion;
            } else {
                selectedVersionTextField.setText("No valid Ballerina SDK found");
                selectedVersionTextField.setForeground(JBColor.RED);
            }
        } else {
            String savedPath = BallerinaSdkSettings.getInstance().getBallerinaSdkPath();
            String savedVersion = BallerinaSdkSettings.getInstance().getBallerinaSdkVersion();
            if (BallerinaSdkUtil.isValidSdk(systemBalPath, systemBalVersion)) {
                sdkVersionComboBox.addItem("System default: " + BallerinaSdkUtil.findBalDistFolder(systemBalPath));
            }
            if (BallerinaSdkUtil.isValidSdk(savedPath, savedVersion)) {
                sdkVersionComboBox.addItem(BallerinaSdkUtil.findBalDistFolder(savedPath));
                sdkVersionComboBox.setSelectedIndex(sdkVersionComboBox.getItemCount() - 1);
                selectedVersionTextField.setText(savedVersion);
                selectedSdkPath = savedPath;
                selectedSdkVersion = savedVersion;
            } else {
                if (BallerinaSdkUtil.isValidSdk(systemBalPath, systemBalVersion)) {
                    sdkVersionComboBox.setSelectedIndex(0);
                    selectedVersionTextField.setText("System Ballerina version: " + systemBalVersion);
                    selectedSdkPath = systemBalPath;
                    selectedSdkVersion = systemBalVersion;
                } else {
                    selectedVersionTextField.setText("No valid Ballerina SDK found");
                    selectedVersionTextField.setForeground(JBColor.RED);
                }
            }
        }
        for (BallerinaSdkUtil.BallerinaSdk sdk : sdkList) {
            if (!Objects.equals(selectedSdkPath, sdk.getPath()) && !Objects.equals(systemBalPath, sdk.getPath())) {
                sdkVersionComboBox.addItem(BallerinaSdkUtil.findBalDistFolder(sdk.getPath()));
            }
        }
        sdkVersionComboBox.addItem("Add Ballerina SDK");
        sdkVersionComboBox.setEnabled(useCustomSdkCheckbox.isSelected());
        useCustomSdkCheckbox.addActionListener(e -> {
            boolean selected = useCustomSdkCheckbox.isSelected();
            if (!selected) {
                String path = sdkVersionComboBox.getItemAt(0);
                if (Objects.equals(path, "System default: " + BallerinaSdkUtil.findBalDistFolder(systemBalPath))) {
                    sdkVersionComboBox.setSelectedIndex(0);
                    selectedVersionTextField.setText("System Ballerina version: "
                            + BallerinaSdkUtil.findBalDistFolder(systemBalVersion));
                } else {
                    sdkVersionComboBox.setSelectedItem("Add Ballerina SDK");
                    selectedVersionTextField.setText("No valid Ballerina SDK found");
                }
            }
            sdkVersionComboBox.setEnabled(selected);
        });
        sdkVersionComboBox.addActionListener(e -> {
            if ("Add Ballerina SDK".equals(sdkVersionComboBox.getSelectedItem())) {
                VirtualFile file =
                        FileChooser.chooseFile(new FileChooserDescriptor(false, true,
                                        false, false,
                                        false, false),
                                project, null);
                if (file != null) {
                    String sdkPath = file.getPath();
                    if (BallerinaSdkUtil.isValidPath(sdkPath)) {
                        sdkPath = Paths.get(sdkPath).normalize().toString();
                        if (sdkPath.equals(systemBalPath)) {
                            sdkVersionComboBox.setSelectedIndex(0);
                            selectedVersionTextField.setText("System Ballerina version: "
                                    + BallerinaSdkUtil.findBalDistFolder(systemBalVersion));
                        } else {
                            selectedSdkPath = sdkPath;
                            sdkVersionComboBox.insertItemAt(BallerinaSdkUtil.findBalDistFolder(sdkPath), 1);
                            sdkVersionComboBox.setSelectedIndex(1);
                            String version = BallerinaSdkUtil.getVersionFromPath(sdkPath);
                            if (!version.isEmpty()) {
                                version = BallerinaSdkUtil.findBalDistFolder(version);
                            } else {
                                version = "Ballerina Swan Lake";
                            }
                            selectedSdkVersion = version;
                            selectedVersionTextField.setText(version);
                        }
                        selectedVersionTextField.setForeground(defaultColor);
                    } else {
                        selectedVersionTextField.setText("Invalid Ballerina SDK path selected");
                        selectedVersionTextField.setForeground(JBColor.RED);
                    }
                }
            } else {
                String path = (String) sdkVersionComboBox.getSelectedItem();
                if (Objects.equals(path, "System default: "
                        + BallerinaSdkUtil.findBalDistFolder(systemBalPath))) {
                    selectedSdkPath = systemBalPath;
                    selectedSdkVersion = systemBalVersion;
                    selectedVersionTextField.setText("System Ballerina version: "
                            + BallerinaSdkUtil.findBalDistFolder(systemBalVersion));
                    selectedVersionTextField.setForeground(defaultColor);
                } else {
                    if (path != null) {
                        path = BallerinaSdkUtil.getBalBatFromDist(path);
                    }
                    String version = BallerinaSdkUtil.getVersionFromPath(path);
                    if (!version.isEmpty()) {
                        version = BallerinaSdkUtil.findBalDistFolder(version);
                    } else {
                        version = "Ballerina Swan Lake";
                    }
                    selectedSdkPath = path;
                    selectedSdkVersion = version;
                    selectedVersionTextField.setText(version);
                    selectedVersionTextField.setForeground(defaultColor);
                }
            }
        });
    }

    public ComboBox<String> getSdkVersionComboBox() {
        return sdkVersionComboBox;
    }

    public JLabel getSelectedVersionTextField() {
        return selectedVersionTextField;
    }

    public JCheckBox getUseCustomSdkCheckbox() {
        return useCustomSdkCheckbox;
    }

    public JLabel getLabel() {
        return label;
    }

    public String getSelectedSdkPath() {
        return selectedSdkPath;
    }

    public String getSelectedSdkVersion() {
        return selectedSdkVersion;
    }

    public void disposeUi() {
        sdkVersionComboBox = null;
        selectedVersionTextField = null;
        useCustomSdkCheckbox = null;
        label = null;
        selectedSdkPath = null;
    }
}
