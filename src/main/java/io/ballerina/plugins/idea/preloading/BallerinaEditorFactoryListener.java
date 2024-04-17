package io.ballerina.plugins.idea.preloading;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFileBase;
import io.ballerina.plugins.idea.BallerinaIcons;
import io.ballerina.plugins.idea.notification.BallerinaNotification;
import io.ballerina.plugins.idea.sdk.BallerinaSdkService;
import io.ballerina.plugins.idea.widget.BallerinaDetectionWidget;
import io.ballerina.plugins.idea.widget.BallerinaDetectionWidgetFactory;
import io.ballerina.plugins.idea.widget.BallerinaIconWidget;
import io.ballerina.plugins.idea.widget.BallerinaIconWidgetFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BallerinaEditorFactoryListener implements EditorFactoryListener {

    private final Project project;
    private boolean balSourcesFound = false;

    public BallerinaEditorFactoryListener(Project project) {
        this.project = project;
    }

    private static boolean isBalFile(@Nullable VirtualFile file) {
        if (file == null || file.getExtension() == null || file instanceof LightVirtualFileBase) {
            return false;
        }
        String fileUrl = file.getUrl();
        if (fileUrl.isEmpty() || fileUrl.startsWith("jar:")) {
            return false;
        }

        return file.getExtension().equals("bal");
    }

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        Project project = event.getEditor().getProject();
        if (project == null) {
            return;
        }
        VirtualFile file = FileDocumentManager.getInstance().getFile(event.getEditor().getDocument());
        if (!balSourcesFound && project.equals(this.project) && isBalFile(file)) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                BallerinaDetectionWidget widget = BallerinaDetectionWidgetFactory.getWidget(project);
                if (widget != null) {
                    ApplicationManager.getApplication().invokeLater(() -> widget.setMessage("Detecting Ballerina.."));
                }
                String balVersion = BallerinaSdkService.getInstance().getBallerinaVersion(project);
                System.out.println(balVersion);
                System.out.println(project.getName());
                if (widget != null) {
                    ApplicationManager.getApplication().invokeLater(() -> widget.setMessage(""));
                }
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (balVersion == null) {
                        BallerinaNotification.notifyBallerinaNotDetected(project);
                    } else {
                        BallerinaIconWidget iconWidget = BallerinaIconWidgetFactory.getWidget(project);
                        if (iconWidget != null) {
                            ApplicationManager.getApplication().invokeLater(() -> {
                                iconWidget.setIcon(BallerinaIcons.FILE);
                                iconWidget.setTooltipText(balVersion);

                            });
                        }
                    }
                });
                balSourcesFound = true;
            });
            balSourcesFound = true;
        }
    }
}
