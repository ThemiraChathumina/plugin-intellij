package io.ballerina.plugins.idea.runconfig.test;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import io.ballerina.plugins.idea.psi.BallerinaPsiUtil;
import io.ballerina.plugins.idea.sdk.BallerinaSdkDetection;
import io.ballerina.plugins.idea.sdk.BallerinaSdkService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class BallerinaTestLineMarkerProvider implements LineMarkerProvider {

    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        String version = BallerinaSdkService.getInstance().getBallerinaVersion();
        if (version != null && isTestFunction(element) && (isPackageTest(element) || isModuleTest(element))) {
            return createTestLineMarkerInfo(element);
        }
        return null;
    }

    private String getFunctionName(PsiElement element) {
        PsiElement root = element.getParent().getParent().getParent();
        PsiElement temp = BallerinaPsiUtil.getNextSibling(root);
        while (temp != null && !temp.toString().equals("PsiElement(BallerinaTokenType.FUNCTION_KEYWORD)")) {
            temp = BallerinaPsiUtil.getNextSibling(temp);
        }
        return temp != null ? BallerinaPsiUtil.getNextSibling(temp).getText() : "";
    }

    private boolean isTestFunction(@NotNull PsiElement element) {
        if (Objects.equals(element.getText(), "Config") &&
                Objects.equals(element.getParent().toString(), "BallerinaAnnotationImpl(ANNOTATION)") &&
                Objects.equals(element.getParent().getParent().toString(), "BallerinaAnnotsImpl(ANNOTS)") &&
                Objects.equals(element.getParent().getParent().getParent().toString(),
                        "BallerinaMetadataImpl(METADATA)") &&
                Objects.equals(element.getParent().getParent().getParent().getParent().toString(),
                        "BallerinaFunctionDefnImpl(FUNCTION_DEFN)") &&
                Objects.equals(element.getParent().getParent().getParent().getParent().getParent().toString(),
                        "BallerinaOtherDeclImpl(OTHER_DECL)")) {
            PsiElement prev1 = BallerinaPsiUtil.getPreviousSibling(element);
            PsiElement prev2 = BallerinaPsiUtil.getPreviousSibling(prev1);
            PsiElement prev3 = BallerinaPsiUtil.getPreviousSibling(prev2);
            if (prev1 != null && prev2 != null && prev3 != null) {
                return prev1.toString().equals("PsiElement(BallerinaTokenType.COLON_TOKEN)") &&
                        prev2.getText().equals("test") &&
                        prev3.toString().equals("PsiElement(BallerinaTokenType.AT_TOKEN)");
            }
        }
        return false;
    }

    private boolean isPackageTest(PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        VirtualFile virtualFile = containingFile != null ? containingFile.getVirtualFile() : null;
        if (virtualFile != null) {
            String path = virtualFile.getPath();
            File current = new File(path);
            File parent = current.getParentFile();
            File balToml = new File(parent.getParent(), "Ballerina.toml");
            return parent.getName().equals("tests") && balToml.exists();
        }
        return false;
    }

    private boolean isModuleTest(PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        VirtualFile virtualFile = containingFile != null ? containingFile.getVirtualFile() : null;
        if (virtualFile != null) {
            String path = virtualFile.getPath();
            File current = new File(path);
            File parent = current.getParentFile();
            File grandParent = parent.getParentFile().getParentFile();
            File balToml = new File(grandParent.getParentFile(), "Ballerina.toml");
            return parent.getName().equals("tests") && grandParent.getName().equalsIgnoreCase("modules") &&
                    balToml.exists();
        }
        return false;
    }

    private String getModuleName(PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        VirtualFile virtualFile = containingFile != null ? containingFile.getVirtualFile() : null;
        if (virtualFile != null) {
            String path = virtualFile.getPath();
            File current = new File(path);
            File parent = current.getParentFile();
            return parent.getParentFile().getName();
        }
        return "";
    }

    private LineMarkerInfo createTestLineMarkerInfo(@NotNull PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        VirtualFile virtualFile = containingFile != null ? containingFile.getVirtualFile() : null;
        String packageName;
        if (virtualFile != null) {
            String path = virtualFile.getPath();
            String packagePath = BallerinaSdkDetection.findBallerinaPackage(path);
            if (!packagePath.isEmpty()) {
                ArrayList<String> pathList = new ArrayList<>(Arrays.asList(packagePath.split("\\\\")));
                packageName = pathList.get(pathList.size() - 1);
            } else {
                packageName = "";
            }
        } else {
            packageName = "";
        }

        String moduleName;
        if (virtualFile != null) {
            String path = virtualFile.getPath();
            if (isModuleTest(element)) {
                moduleName = getModuleName(element);
            } else {
                moduleName = "";
            }
        } else {
            moduleName = "";
        }

        return new LineMarkerInfo<>(element, element.getTextRange(), AllIcons.RunConfigurations.TestState.Run,
                // Default run icon
                psiElement -> "Test " + getFunctionName(element), // Tooltip text when hovering over the run icon
                (e, elt) -> {

                    e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    Project project = elt.getProject();
                    VirtualFile file = elt.getContainingFile().getVirtualFile();

                    if (file != null && file.getName().endsWith(".bal")) {

                        // Get the RunManager and create a new configuration
                        RunManager runManager = RunManager.getInstance(project);
                        String configName = !packageName.isEmpty() ? packageName : "finalFileName";
                        String temp = configName.endsWith(".bal") ? configName.substring(0, configName.length() - 4) :
                                configName;
                        RunnerAndConfigurationSettings settings =
                                runManager.createConfiguration(temp, BallerinaTestConfigurationType.class);
                        BallerinaTestConfiguration testConfiguration =
                                (BallerinaTestConfiguration) settings.getConfiguration();
                        String script = file.getPath();
                        String ballerinaPackage = BallerinaSdkDetection.findBallerinaPackage(script);
                        if (!ballerinaPackage.isEmpty()) {
                            script = ballerinaPackage;
                        }
                        testConfiguration.setScriptName(script);
//                        testConfiguration.addCommand("--tests");
                        if (isModuleTest(element)) {
                            String cmd = "--tests " + packageName + "." + moduleName + ":" + getFunctionName(element);
                            testConfiguration.addCommand(cmd);
                        } else {
                            testConfiguration.addCommand("--tests " + getFunctionName(element));
                        }

                        try {
                            ExecutionEnvironmentBuilder.create(project, DefaultRunExecutor.getRunExecutorInstance(),
                                    testConfiguration).buildAndExecute();
                        } catch (ExecutionException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }, GutterIconRenderer.Alignment.CENTER, () -> "Test " + getFunctionName(element)
                // Fallback tooltip text
        );
    }
}
