package org.intellij.sdk.language;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BallerinaFileEditorManagerListener implements FileEditorManagerListener {

    private final Project project;

    public BallerinaFileEditorManagerListener(Project project) {
        this.project = project;
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (file.getName().endsWith(".bal")) {
            System.out.println("Ballerina file opened: " + file.getName());
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Ballerina");
            if (toolWindow != null) {
                toolWindow.show();
            }
        }
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (Objects.requireNonNull(ToolWindowManager.getInstance(project).getToolWindow("Ballerina")).getContentManager().getContentCount() <= 0) {
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Ballerina");
            if (toolWindow != null) {
                toolWindow.hide(null);
            }
        }
    }
}
