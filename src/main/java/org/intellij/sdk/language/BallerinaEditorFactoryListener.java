package org.intellij.sdk.language;


import com.intellij.ide.BrowserUtil;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.testFramework.LightVirtualFileBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.lsp4intellij.IntellijLanguageClient;
import org.wso2.lsp4intellij.client.languageserver.serverdefinition.ProcessBuilderServerDefinition;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BallerinaEditorFactoryListener implements EditorFactoryListener {

    private Project project;
    private boolean balSourcesFound = false;


    public BallerinaEditorFactoryListener(Project project) {
        this.project = project;
    }

    // Todo: check for mac
    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        Project project = event.getEditor().getProject();
        if (project == null) {
            return;
        }
        VirtualFile file = FileDocumentManager.getInstance().getFile(event.getEditor().getDocument());
//        System.out.println("editorCreated: ");
        if (!balSourcesFound && project.equals(this.project) && isBalFile(file)) {
            doRegister(project);
//            System.out.println("editorCreated: isBalFile");
                ApplicationManager.getApplication().executeOnPooledThread(() -> {
//                    System.out.println("editorCreated: executeOnPooledThread");
                    // change message of the detection widget to "Detecting Ballerina" here.
                    BallerinaDetectionWidget widget = BallerinaDetectionWidgetFactory.getWidget(project);
                    if (widget != null) {
                        ApplicationManager.getApplication().invokeLater(() -> widget.setMessage("Detecting Ballerina.."));
                    }
                    String balVersion = BallerinaSdkService.getInstance().getBallerinaVersion();
//                    System.out.println("editorCreated: balVersion: " + balVersion);
                    // change message of the detection widget to "" here.
                    if (widget != null) {
                        ApplicationManager.getApplication().invokeLater(() -> widget.setMessage(""));
                    }
                    ApplicationManager.getApplication().invokeLater(() -> {
//                        System.out.println("editorCreated: invokeLater");
                        if (balVersion == null){
//                            System.out.println("editorCreated: balVersion is null");
                            BallerinaNotification.notifyBallerinaNotDetected(project);
                        } else {
//                            System.out.println("editorCreated: balVersion is not null");
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

    private static void doRegister(@NotNull Project project) {
        System.out.println("doRegister: ");
        List<String> args = new ArrayList<>();
        args.add("C:\\Program Files\\Ballerina\\distributions\\ballerina-2201.8.4\\bin\\bal.bat");
        args.add("start-language-server");
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        processBuilder.directory(new File(Objects.requireNonNull(project.getBasePath())));

        // Registers language server definition in the lsp4intellij lang-client library.
        IntellijLanguageClient.addServerDefinition(new ProcessBuilderServerDefinition("bal", processBuilder),
                project);
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
}
