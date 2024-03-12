package org.intellij.sdk.language;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

public class BallerinaFileOpenListener implements FileEditorManagerListener {
    private Project project;

    public BallerinaFileOpenListener(Project project) {
        this.project = project;
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (file.getName().endsWith(".bal")) {
            Messages.showMessageDialog(project, "Ballerina file opened: " + file.getName(), "Information", Messages.getInformationIcon());
        }
    }

    public static void register(Project project) {
        MessageBusConnection connection = project.getMessageBus().connect(project);
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new BallerinaFileOpenListener(project));
    }
}
