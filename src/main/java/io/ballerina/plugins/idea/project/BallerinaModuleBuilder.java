package io.ballerina.plugins.idea.project;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import io.ballerina.plugins.idea.sdk.BallerinaSettingsService;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public class BallerinaModuleBuilder extends ModuleBuilder {

    private String sdkVersion;

    private String sdkPath;

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public void setSdkPath(String sdkPath) {
        this.sdkPath = sdkPath;
    }

    @Override
    public void setupRootModel(ModifiableRootModel modifiableRootModel) {
        Project project = modifiableRootModel.getProject();
        final VirtualFile projectRoot =
                LocalFileSystem.getInstance().refreshAndFindFileByPath(Objects.requireNonNull(getContentEntryPath()));

        if (projectRoot != null) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    VirtualFile testFolder = projectRoot.createChildDirectory(this, "test");
                    VirtualFile mainBal = projectRoot.createChildData(this, "main.bal");
                    VirtualFile testBal = testFolder.createChildData(this, "test.bal");
                    // Optionally, you can write initial content into these files
//                    mainBal.setBinaryContent("/* Your main.bal file content here */".getBytes());
//                    testBal.setBinaryContent("/* Your test.bal file content here */".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();  // Proper logging or error handling should be implemented here
                }
            });
        }

        // Setting up the SDK settings for the project
        if (sdkVersion != null) {
            BallerinaSettingsService settingsService = BallerinaSettingsService.getInstance(project);
            settingsService.setBallerinaVersion(sdkVersion);
            settingsService.setBallerinaRuntimePath(sdkPath);
        }
    }

    @Override
    public BallerinaModuleType getModuleType() {
        return BallerinaModuleType.getInstance();
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new BallerinaModuleWizardStep(this);
    }

}
