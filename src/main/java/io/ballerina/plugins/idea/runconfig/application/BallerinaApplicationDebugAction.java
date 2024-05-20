package io.ballerina.plugins.idea.runconfig.application;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import io.ballerina.plugins.idea.BallerinaIcons;
import io.ballerina.plugins.idea.debugger.BallerinaDebugger;
import io.ballerina.plugins.idea.project.BallerinaProjectUtils;
import io.ballerina.plugins.idea.sdk.BallerinaSdkService;
import io.ballerina.plugins.idea.sdk.BallerinaSdkUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.Optional;

import static io.ballerina.plugins.idea.BallerinaConstants.BAL_EXTENSION;

public class BallerinaApplicationDebugAction extends AnAction {

    public BallerinaApplicationDebugAction() {
        super(BallerinaIcons.DEBUG);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (project != null && file != null && (file.getName().endsWith(BAL_EXTENSION)
                | file.isDirectory())) {
            String fileName = file.getName();
            String path = BallerinaSdkUtils.getNormalizedPath(file.getPath());
            Optional<String> packagePath = BallerinaProjectUtils.findBallerinaPackage(path);
            if (packagePath.isPresent()) {
                fileName = Paths.get(packagePath.get()).normalize().getFileName().toString();
            }
            RunManager runManager = RunManager.getInstance(project);
            String temp = fileName.endsWith(BAL_EXTENSION)
                    ? fileName.substring(0, fileName.length() - BAL_EXTENSION.length()) : fileName;
            RunnerAndConfigurationSettings settings =
                    runManager.createConfiguration("Run " + temp, BallerinaApplicationRunConfigType.class);
            BallerinaApplicationRunConfiguration runConfiguration =
                    (BallerinaApplicationRunConfiguration) settings.getConfiguration();
            String script = BallerinaSdkUtils.getNormalizedPath(file.getPath());
            Optional<String> ballerinaPackage = BallerinaProjectUtils.findBallerinaPackage(script);
            if (ballerinaPackage.isPresent()) {
                script = ballerinaPackage.get();
            }
            runConfiguration.setSourcePath(script);

            boolean configExists = false;
            for (RunConfiguration existingConfig : runManager.getAllConfigurationsList()) {
                if (existingConfig instanceof BallerinaApplicationRunConfiguration &&
                        existingConfig.getName().equals(runConfiguration.getName()) &&
                        ((BallerinaApplicationRunConfiguration) existingConfig).getSourcePath()
                                .equals(runConfiguration.getSourcePath())) {
                    configExists = true;
                    settings = runManager.findSettings(existingConfig);
                    runConfiguration = (BallerinaApplicationRunConfiguration) existingConfig;
                    break;
                }
            }

            if (!configExists) {
                runManager.addConfiguration(settings);
            }

            runManager.setSelectedConfiguration(settings);

            try {
                ExecutionEnvironmentBuilder.create(project, DefaultRunExecutor.getRunExecutorInstance(),
                        runConfiguration).runner(new BallerinaDebugger()).buildAndExecute();

            } catch (ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        String version = BallerinaSdkService.getInstance().getBallerinaVersion(e.getProject());
        boolean visible =
                file != null && (file.getName().endsWith(BAL_EXTENSION) | file.isDirectory())
                        && !version.isEmpty();
        if (visible) {
            String fileName = "\"" + file.getName() + "\"";
            String path = BallerinaSdkUtils.getNormalizedPath(file.getPath());
            Optional<String> packagePath = BallerinaProjectUtils.findBallerinaPackage(path);
            String packageName = null;
            if (packagePath.isPresent()) {
                packageName = Paths.get(packagePath.get()).normalize().getFileName().toString();
                fileName = "package " + "\"" + packageName + "\"";
            }
            if (!file.isDirectory() || (file.isDirectory() && file.getName().equals(packageName))) {
                e.getPresentation().setVisible(true);
                e.getPresentation().setText("Debug " + fileName);
            } else {
                e.getPresentation().setVisible(false);
            }
        } else {
            e.getPresentation().setVisible(false);
        }
    }
}
