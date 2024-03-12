package org.intellij.sdk.language;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.sdk.language.psi.BallerinaOtherDecl;
import org.intellij.sdk.language.psi.BallerinaTypes;
import org.intellij.sdk.language.psi.impl.BallerinaFunctionDefnImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class BallerinaRunFileAction extends AnAction {

    public BallerinaRunFileAction() {
        super(BallerinaIcons.FILE);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (project != null && file != null && (file.getName().endsWith(".bal") | file.isDirectory())) {

            String fileName = file.getName();
            String path = file.getPath();
            String packagePath = BallerinaSdkDetection.findBallerinaPackage(path);
            if (!packagePath.isEmpty()) {
                ArrayList<String> pathList = new ArrayList<>(Arrays.asList(packagePath.split("\\\\")));
                fileName = pathList.get(pathList.size() - 1);
            }
            RunManager runManager = RunManager.getInstance(project);
            String temp = fileName.endsWith(".bal") ? fileName.substring(0, fileName.length() - 4) : fileName;
            RunnerAndConfigurationSettings settings = runManager.createConfiguration(temp, BallerinaRunConfigurationType.class);
            BallerinaRunConfiguration runConfiguration = (BallerinaRunConfiguration) settings.getConfiguration();
            String script =  file.getPath();
            String ballerinaPackage = BallerinaSdkDetection.findBallerinaPackage(script);
            if (!ballerinaPackage.isEmpty()) {
                script = ballerinaPackage;
            }
            runConfiguration.setScriptName(script);

            // Check if similar configuration already exists, if not, add the new one
            boolean exists = false;
            for (RunConfiguration existingConfig : runManager.getAllConfigurationsList()) {
                if (existingConfig instanceof BallerinaRunConfiguration &&
                        existingConfig.getName().equals(runConfiguration.getName()) &&
                        ((BallerinaRunConfiguration) existingConfig).getScriptName().equals(runConfiguration.getScriptName())) {
                    exists = true;
                    settings = runManager.findSettings(existingConfig);
                    break;
                }
            }


            if (!exists) {
                runManager.addConfiguration(settings);
            }

            // Select the new configuration in the UI
            runManager.setSelectedConfiguration(settings);

            try {
                ExecutionEnvironmentBuilder.create(project, DefaultRunExecutor.getRunExecutorInstance(), runConfiguration)
                        .buildAndExecute();

            } catch (ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }



    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        String version = BallerinaSdkService.getInstance().getBallerinaVersion();
        boolean visible = file != null && (file.getName().endsWith(".bal") | file.isDirectory()) && version != null;
        if (visible) {
            String fileName = file.getName();
            String path = file.getPath();
            String packagePath = BallerinaSdkDetection.findBallerinaPackage(path);
            String packageName = null;
            if (!packagePath.isEmpty()) {
                ArrayList<String> pathList = new ArrayList<>(Arrays.asList(packagePath.split("\\\\")));
                packageName = pathList.get(pathList.size() - 1);
                fileName = "package " + packageName;
            }
            if (!file.isDirectory() || (file.isDirectory() && file.getName().equals(packageName))) {
                e.getPresentation().setVisible(true);
                e.getPresentation().setText("Run " + fileName);
            } else {
                e.getPresentation().setVisible(false);
            }
        } else {
            e.getPresentation().setVisible(false);
        }
    }
}

