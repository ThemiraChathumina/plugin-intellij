package io.ballerina.plugins.idea.project;

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.platform.ProjectGeneratorPeer;
import io.ballerina.plugins.idea.BallerinaIcons;
import io.ballerina.plugins.idea.sdk.BallerinaSdkDetection;
import io.ballerina.plugins.idea.sdk.BallerinaSdkService;
import io.ballerina.plugins.idea.sdk.BallerinaSettingsService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import javax.swing.*;

public class BallerinaProjectGenerator extends WebProjectTemplate<Object> {

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
    public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull Object settings,
                                @NotNull Module module) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                VirtualFile srcDir = baseDir.createChildDirectory(this, "src");
                srcDir.createChildData(this, "main.bal");
//                String selectedVersion = settings.toString();  // This will use the SDK version selected in the Peer
//                BallerinaSettingsService.getInstance(project).setBallerinaVersion(selectedVersion);
//                String sdkPath = BallerinaSdkDetection.getSdkPath(selectedVersion);  // Assume SdkManager can provide the path for the version
//                BallerinaSettingsService.getInstance(project).setBallerinaRuntimePath(sdkPath);
            } catch (IOException e) {
                e.printStackTrace();  // Consider proper logging or user notification
            }

            String projectName = project.getName();
            String projectBasePath = Paths.get(Objects.requireNonNull(project.getBasePath()))
                    .normalize().getParent().toString();

            String imlFilePath = projectBasePath + "/" + projectName + ".iml";
            VirtualFile imlFile = VirtualFileManager.getInstance().findFileByUrl("file://" + imlFilePath);

            if (imlFile != null) {
                try {
                    imlFile.delete(this);
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        });
    }

    @NotNull
    @Override
    public ProjectGeneratorPeer<Object> createPeer() {
        return new BallerinaProjectGeneratorPeer();
    }

    public class BallerinaProjectGeneratorPeer implements ProjectGeneratorPeer<Object> {
        private final JComboBox<String> sdkComboBox;
        private final JPanel panel;

        public BallerinaProjectGeneratorPeer() {
            sdkComboBox = new JComboBox<>();
            panel = new JPanel();
            loadSdks();  // Load available SDKs into the combo box
            panel.add(new JLabel("SDK:"));
            panel.add(sdkComboBox);
        }

        private void loadSdks() {
            List<BallerinaSdkDetection.MiniSdk> sdks = BallerinaSdkDetection.getSdks();  // Assume SdkManager provides the getSdks method
            for (BallerinaSdkDetection.MiniSdk sdk : sdks) {
                sdkComboBox.addItem(sdk.getVersion());
            }
        }

        @Override
        public @NotNull JComponent getComponent() {
            return panel;
        }

        @Override
        public void buildUI(@NotNull SettingsStep settingsStep) {
            settingsStep.addSettingsComponent(panel);
        }

        @Override
        public @NotNull Object getSettings() {
            return sdkComboBox.getSelectedItem();  // Return the selected SDK version
        }

        @Override
        public @Nullable ValidationInfo validate() {
            if (sdkComboBox.getSelectedItem() == null) {
                return new ValidationInfo("Please select a Ballerina SDK", sdkComboBox);
            }
            return null;
        }

        @Override
        public boolean isBackgroundJobRunning() {
            return false;
        }
    }

}