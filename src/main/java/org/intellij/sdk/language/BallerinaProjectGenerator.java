package org.intellij.sdk.language;

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.externalSystem.model.project.ProjectSdkData;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.ProjectGeneratorPeer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class BallerinaProjectGenerator extends WebProjectTemplate<Object> {

    private Sdk sdk;

    @Override
    public @NlsContexts.DetailedDescription String getDescription() {
        return "Create a new Ballerina project.";
    }

    @Override
    public @NotNull @NlsContexts.Label String getName() {
        return "Ballerina";
    }

    @Override
    public @NotNull Icon getIcon() {
        return BallerinaIcons.FILE; // Ensure BallerinaIcons.FILE is correctly defined and accessible
    }

    @Override
    public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull Object settings, @NotNull Module module) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            // Set up the Ballerina SDK for the project
            ModifiableRootModel rootModel = ModuleRootManager.getInstance(module).getModifiableModel();
//            sdk = ProjectJdkTable.getInstance().createSdk("Ballerina SDK", BallerinaSdkType.getInstance());
//            sdk.getSdkModificator().setVersionString(BallerinaSdkService.getInstance().getBallerinaVersion());
//            sdk.getSdkModificator().setHomePath(new File(BallerinaSdkService.getInstance().getBallerinaPath()).getParentFile().getAbsolutePath());
//            sdk.getSdkModificator().commitChanges();
//            rootModel.setSdk(sdk);
            // Implement the logic to create Ballerina package structure here
            VirtualFile srcDir = null;
            try {
                srcDir = baseDir.createChildDirectory(this, "src");
                VirtualFile mainBal = srcDir.createChildData(this, "main.bal");
                // Write default content to 'main.bal' if necessary
            } catch (IOException e) {
                e.printStackTrace(); // Consider proper logging or user notification
            }

            rootModel.commit();
        });
    }

    @NotNull
    @Override
    public ProjectGeneratorPeer<Object> createPeer() {
        return new BallerinaProjectGeneratorPeer();
    }

    public void setSdk(Sdk sdk) {
        this.sdk = sdk;
    }



    public static class BallerinaProjectGeneratorPeer implements ProjectGeneratorPeer<Object> {
        private final JLabel sdkNameLabel;

        public BallerinaProjectGeneratorPeer() {
            sdkNameLabel = new JLabel();
            updateSdkName();
        }

        private void updateSdkName() {
            String sdk = BallerinaSdkService.getInstance().getBallerinaVersion();
            sdkNameLabel.setText(sdk != null ? sdk : "No Ballerina SDK selected");
        }

        @Override
        public @NotNull JComponent getComponent() {
            // Create a panel to hold the SDK name label
            JPanel panel = new JPanel();
            panel.add(sdkNameLabel);
            return panel;
        }

        @Override
        public void buildUI(@NotNull SettingsStep settingsStep) {
            // Add the SDK name label to the settings step UI
            settingsStep.addSettingsField("SDK: ", sdkNameLabel);
        }

        @Override
        public @NotNull Object getSettings() {
            return new Object(); // Return the settings object, if any
        }

        @Override
        public @Nullable ValidationInfo validate() {
            // Implement any validation logic here, return null if validation is successful
            return null;
        }

        @Override
        public boolean isBackgroundJobRunning() {
            // Return true if there's a background process that needs to complete before finishing the wizard
            return false;
        }
    }
}
