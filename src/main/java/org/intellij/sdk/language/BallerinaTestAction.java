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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.sdk.language.psi.BallerinaOtherDecl;
import org.intellij.sdk.language.psi.BallerinaTypes;
import org.intellij.sdk.language.psi.impl.BallerinaFunctionDefnImpl;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class BallerinaTestAction extends AnAction {

    public BallerinaTestAction() {
        super(BallerinaIcons.FILE);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (project == null || file == null || !file.getName().endsWith(".bal")) {
            return;
        }

        String path = file.getPath();
        String packagePath = BallerinaSdkDetection.findBallerinaPackage(path);
        String modulePath = BallerinaSdkDetection.findBallerinaModule(path);
        String fileName = extractFileName(packagePath, modulePath, file.getName());

        RunManager runManager = RunManager.getInstance(project);
        String configName = getConfigName(fileName);
        RunnerAndConfigurationSettings settings = getConfigurationSettings(runManager, configName, project, path, packagePath, modulePath);

        // Check if similar configuration already exists, if not, add the new one
        if (!configurationExists(runManager, settings.getConfiguration())) {
            runManager.addConfiguration(settings);
        }

        // Select the new configuration in the UI
        runManager.setSelectedConfiguration(settings);

        executeConfiguration(project, settings.getConfiguration());
    }

    private String extractFileName(String packagePath, String modulePath, String defaultName) {
        String finalPath = !modulePath.isEmpty() ? modulePath : packagePath;
        if (!finalPath.isEmpty()) {
            ArrayList<String> pathList = new ArrayList<>(Arrays.asList(finalPath.split("\\\\")));
            return pathList.get(pathList.size() - 1);
        }
        return defaultName;
    }

    private String getConfigName(String fileName) {
        return "Test " + (fileName.endsWith(".bal") ? fileName.substring(0, fileName.length() - 4) : fileName);
    }

    private RunnerAndConfigurationSettings getConfigurationSettings(RunManager runManager, String configName, Project project, String scriptPath, String packagePath, String modulePath) {
        RunnerAndConfigurationSettings settings = runManager.createConfiguration(configName, BallerinaTestConfigurationType.class);
        BallerinaTestConfiguration runConfiguration = (BallerinaTestConfiguration) settings.getConfiguration();
        String script = scriptPath;
        String cmd = "";

        if (!modulePath.isEmpty()) {
            script = modulePath;
            cmd = new File(packagePath).getName() + "." + new File(script).getName() + ":*";
        } else if (!packagePath.isEmpty()) {
            script = packagePath;
            cmd = new File(packagePath).getName() + ":*";
        }

        runConfiguration.setScriptName(script);

        runConfiguration.addCommand("--tests " + cmd);

        return settings;
    }

    private boolean configurationExists(RunManager runManager, RunConfiguration runConfiguration) {
        for (RunConfiguration existingConfig : runManager.getAllConfigurationsList()) {
            if (existingConfig instanceof BallerinaTestConfiguration &&
                    existingConfig.getName().equals(runConfiguration.getName()) &&
                    ((BallerinaTestConfiguration) existingConfig).getScriptName().equals(((BallerinaTestConfiguration) runConfiguration).getScriptName())) {
                return true;
            }
        }
        return false;
    }

    private void executeConfiguration(Project project, RunConfiguration runConfiguration) {
        try {
            ExecutionEnvironmentBuilder.create(project, DefaultRunExecutor.getRunExecutorInstance(), runConfiguration)
                    .buildAndExecute();
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }




    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        String version = BallerinaSdkService.getInstance().getBallerinaVersion();

        // Early return if basic conditions are not met
        if (file == null || !file.getName().endsWith(".bal") || version == null) {
            e.getPresentation().setVisible(false);
            return;
        }

        String path = file.getPath();
        String modulePath = BallerinaSdkDetection.findBallerinaModule(path);
        String packagePath = BallerinaSdkDetection.findBallerinaPackage(path);

        // Try to set visibility based on module tests
        boolean visibilitySet = setVisibilityForModuleTests(e, modulePath);

        // If not set by module tests, try to set based on package tests
        if (!visibilitySet) {
            setVisibilityForPackageTests(e, packagePath);
        }
    }

    private boolean setVisibilityForModuleTests(@NotNull AnActionEvent e, String modulePath) {
        if (!modulePath.isEmpty()) {
            File moduleTests = new File(modulePath, "tests");
            if (moduleTests.exists() && moduleTests.isDirectory()) {
                String moduleName = new File(modulePath).getName();
                setTestVisibility(e, "module " + moduleName);
                return true;
            }
        }
        return false;
    }

    private void setVisibilityForPackageTests(@NotNull AnActionEvent e, String packagePath) {
        if (!packagePath.isEmpty()) {
            File tests = new File(packagePath, "tests");
            if (tests.exists() && tests.isDirectory()) {
                String packageName = new File(packagePath).getName();
                setTestVisibility(e, "package " + packageName);
            } else {
                e.getPresentation().setVisible(false);
            }
        } else {
            e.getPresentation().setVisible(false);
        }
    }

    private void setTestVisibility(@NotNull AnActionEvent e, String testName) {
        e.getPresentation().setVisible(true);
        e.getPresentation().setText("Test " + testName);
    }

}

