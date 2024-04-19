package io.ballerina.plugins.idea.configuration;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.JBUI;
import io.ballerina.plugins.idea.configuration.ui.BallerinaSdkSelection;
import io.ballerina.plugins.idea.notification.BallerinaNotification;
import io.ballerina.plugins.idea.sdk.BallerinaSdkService;
import io.ballerina.plugins.idea.sdk.BallerinaSdkSettings;
import io.ballerina.plugins.idea.sdk.BallerinaSdkUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Objects;
import java.util.Timer;

import javax.swing.JComponent;
import javax.swing.JPanel;
public class BallerinaLanguageSettingsConfigurable implements Configurable {

    private final Project project;
    private boolean modified = false;

    private BallerinaSdkSelection sdkSelectionUI;

    public BallerinaLanguageSettingsConfigurable(Project project) {
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

        sdkSelectionUI = new BallerinaSdkSelection(project);
        sdkSelectionUI.sdkSelection();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0;  // Do not allow vertical stretching
        gbc.insets = JBUI.insets(2, 0);
        panel.add(sdkSelectionUI.getLabel(), gbc);

        // Add UI components here
        gbc.gridy = 1;
        panel.add(sdkSelectionUI.getSdkVersionComboBox(), gbc);

        gbc.gridy = 2;
        panel.add(sdkSelectionUI.getSelectedVersionTextField(), gbc);

        gbc.gridy = 3;
        panel.add(sdkSelectionUI.getUseCustomSdkCheckbox(), gbc);

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
        boolean isCustomSdkSelected = sdkSelectionUI.getUseCustomSdkCheckbox().isSelected();
        String selectedSdkPath = BallerinaSdkUtil.findBalDistFolder(sdkSelectionUI.getSelectedSdkPath());
        String currentSdkPath
                = BallerinaSdkUtil.findBalDistFolder(BallerinaSdkSettings.getInstance().getBallerinaSdkPath());
        String systemSdkPath
                = BallerinaSdkUtil.findBalDistFolder(BallerinaSdkService.getInstance().getSystemBalPath());
        if (isCustomSdkSelected != BallerinaSdkSettings.getInstance().isUseCustomSdk()) {
            modified = true;
        }
        BallerinaSdkSettings.getInstance().setUseCustomSdk(isCustomSdkSelected);
        // Check if the custom SDK checkbox is selected and paths are different
        if (isCustomSdkSelected) {
            if (!Objects.equals(currentSdkPath, selectedSdkPath)) {
                String path = sdkSelectionUI.getSelectedSdkPath();
                String version = sdkSelectionUI.getSelectedSdkVersion();
                BallerinaSdkService.getInstance().setBallerinaSdk(path, version);
            }
            if (Objects.equals(selectedSdkPath, systemSdkPath)) {
                BallerinaSdkSettings.getInstance().setUseCustomSdk(false);
            }
        }
    }

    // Todo: disconnect LSP process, update widget
    private void reloadEditor() {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        VirtualFile[] openedFiles = fileEditorManager.getOpenFiles();

        for (VirtualFile file : openedFiles) {
            ApplicationManager.getApplication().invokeLater(() -> {
                fileEditorManager.closeFile(file);

                Timer timer = new Timer();
                timer.schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            fileEditorManager.openFile(file, true);
                        });
                    }
                }, 200);
            });
        }
    }

    @Override
    public void disposeUIResources() {
        sdkSelectionUI.disposeUi();
        if (modified) {
//            reloadEditor();
            BallerinaNotification.notifyRestartIde(project);
        }
    }
}
