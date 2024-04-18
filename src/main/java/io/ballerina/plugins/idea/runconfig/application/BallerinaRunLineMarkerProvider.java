package io.ballerina.plugins.idea.runconfig.application;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import io.ballerina.plugins.idea.project.BallerinaProjectUtil;
import io.ballerina.plugins.idea.sdk.BallerinaSdkService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class BallerinaRunLineMarkerProvider implements LineMarkerProvider {

    @Nullable

    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        String version = BallerinaSdkService.getInstance().getBallerinaVersion();
        if (version != null && isMainFunction(element) || isService(element)) {
            return createRunLineMarkerInfo(element);
        }
        return null;
    }

    private boolean checkPublicFunction(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        return parent.getFirstChild().getNextSibling().toString()
                .equals("PsiElement(BallerinaTokenType.PUBLIC_KEYWORD)");
    }

    private boolean isMainFunction(@NotNull PsiElement element) {
        PsiElement prevSibling = element.getPrevSibling();
        while (prevSibling != null && prevSibling.toString().equals("PsiWhiteSpace")) {
            prevSibling = prevSibling.getPrevSibling();
        }
        return element.getText().equals("main") && prevSibling != null && Objects.requireNonNull(prevSibling).toString()
                .equals("PsiElement(BallerinaTokenType.FUNCTION_KEYWORD)") && element.getParent() != null &&
                Objects.requireNonNull(element.getParent()).toString()
                        .equals("BallerinaFunctionDefnImpl(FUNCTION_DEFN)") && checkPublicFunction(element) &&
                element.getParent().getParent() != null &&
                Objects.requireNonNull(element.getParent().getParent()).toString()
                        .equals("BallerinaOtherDeclImpl(OTHER_DECL)");
    }

    private boolean isService(@NotNull PsiElement element) {
        return Objects.requireNonNull(element).toString().equals("PsiElement(BallerinaTokenType.SERVICE_KEYWORD)") &&
                element.getParent() != null && Objects.requireNonNull(element.getParent()).toString()
                .equals("BallerinaServiceDeclImpl(SERVICE_DECL)") && element.getParent().getParent() != null &&
                Objects.requireNonNull(element.getParent().getParent()).toString()
                        .equals("BallerinaOtherDeclImpl(OTHER_DECL)");
    }

    private LineMarkerInfo createRunLineMarkerInfo(@NotNull PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        String fileName = containingFile != null ? containingFile.getName() : "Unknown";
        VirtualFile virtualFile = containingFile != null ? containingFile.getVirtualFile() : null;
        String packageName;
        if (virtualFile != null) {
            String path = virtualFile.getPath();
            String packagePath = BallerinaProjectUtil.findBallerinaPackage(path);
            if (!packagePath.isEmpty()) {
                ArrayList<String> pathList = new ArrayList<>(Arrays.asList(packagePath.split("\\\\")));
                packageName = pathList.get(pathList.size() - 1);
                fileName = "package " + packageName;
            } else {
                packageName = "";
            }
        } else {
            packageName = "";
        }
        String finalFileName = fileName;
        return new LineMarkerInfo<>(element, element.getTextRange(), AllIcons.RunConfigurations.TestState.Run,
                // Default run icon
                psiElement -> "Run " + finalFileName, // Tooltip text when hovering over the run icon
                (e, elt) -> {

                    e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    Project project = elt.getProject();
                    VirtualFile file = elt.getContainingFile().getVirtualFile();

                    if (file != null && file.getName().endsWith(".bal")) {

                        // Get the RunManager and create a new configuration
                        RunManager runManager = RunManager.getInstance(project);
                        String configName = !packageName.isEmpty() ? packageName : finalFileName;
                        String temp = configName.endsWith(".bal") ? configName.substring(0, configName.length() - 4) :
                                configName;
                        RunnerAndConfigurationSettings settings =
                                runManager.createConfiguration(temp, BallerinaApplicationRunConfigType.class);
                        BallerinaApplicationRunConfiguration runConfiguration =
                                (BallerinaApplicationRunConfiguration) settings.getConfiguration();
                        String script = file.getPath();
                        String ballerinaPackage = BallerinaProjectUtil.findBallerinaPackage(script);
                        if (!ballerinaPackage.isEmpty()) {
                            script = ballerinaPackage;
                        }
                        runConfiguration.setScriptName(script);

                        // Check if similar configuration already exists, if not, add the new one
                        boolean exists = false;
                        for (RunConfiguration existingConfig : runManager.getAllConfigurationsList()) {
                            if (existingConfig instanceof BallerinaApplicationRunConfiguration &&
                                    existingConfig.getName().equals(runConfiguration.getName()) &&
                                    ((BallerinaApplicationRunConfiguration) existingConfig).getScriptName()
                                            .equals(runConfiguration.getScriptName())) {
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
                            ExecutionEnvironmentBuilder.create(project, DefaultRunExecutor.getRunExecutorInstance(),
                                    runConfiguration).buildAndExecute();
                        } catch (ExecutionException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }, GutterIconRenderer.Alignment.CENTER, () -> "Run " + finalFileName // Fallback tooltip text
        );
    }

}
