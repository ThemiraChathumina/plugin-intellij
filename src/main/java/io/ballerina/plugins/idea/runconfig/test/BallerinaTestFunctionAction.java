/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.ballerina.plugins.idea.runconfig.test;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import io.ballerina.plugins.idea.BallerinaIcons;
import io.ballerina.plugins.idea.debugger.BallerinaDebugger;
import io.ballerina.plugins.idea.project.BallerinaProjectUtils;
import io.ballerina.plugins.idea.psi.BallerinaPsiUtil;
import io.ballerina.plugins.idea.sdk.BallerinaSdkUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.Optional;

import static io.ballerina.plugins.idea.BallerinaConstants.BAL_EXTENSION;
import static io.ballerina.plugins.idea.BallerinaConstants.EMPTY_STRING;

/**
 * Represents Ballerina test action for running and debugging Ballerina tests for individual test functions.
 *
 * @since 2.0.0
 */
public class BallerinaTestFunctionAction extends AnAction {

    private final PsiElement element;
    private boolean isDebugging;

    public BallerinaTestFunctionAction(PsiElement element, boolean isDebugging) {
        super(isDebugging ? BallerinaIcons.DEBUG : BallerinaIcons.RUN);
        this.element = element;
        this.isDebugging = isDebugging;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        PsiFile containingFile = element.getContainingFile();
        VirtualFile file = containingFile != null ? containingFile.getVirtualFile() : null;
        String packageName;
        if (file != null) {
            String path = file.getPath();
            Optional<String> packagePath = BallerinaProjectUtils.findBallerinaPackage(path);
            packageName = packagePath.map(s -> Paths.get(s).normalize().getFileName().toString()).orElse(EMPTY_STRING);
        } else {
            packageName = EMPTY_STRING;
        }

        String moduleName;
        if (file != null) {
            if (BallerinaProjectUtils.isModuleTest(element)) {
                Optional<String> module = BallerinaProjectUtils.getModuleName(element);
                moduleName = module.orElse(EMPTY_STRING);
            } else {
                moduleName = EMPTY_STRING;
            }
        } else {
            moduleName = EMPTY_STRING;
        }
        if (file != null && file.getName().endsWith(BAL_EXTENSION)) {

            RunManager runManager = RunManager.getInstance(project);
            String configName = !packageName.isEmpty() ? packageName : "finalFileName";
            String temp = configName.endsWith(BAL_EXTENSION)
                    ? configName.substring(0, configName.length() - BAL_EXTENSION.length()) : configName;
            RunnerAndConfigurationSettings settings =
                    runManager.createConfiguration(temp, BallerinaTestConfigurationType.class);
            BallerinaTestConfiguration testConfiguration =
                    (BallerinaTestConfiguration) settings.getConfiguration();
            String script = BallerinaSdkUtils.getNormalizedPath(file.getPath());
            Optional<String> ballerinaPackage = BallerinaProjectUtils.findBallerinaPackage(script);
            if (ballerinaPackage.isPresent()) {
                script = ballerinaPackage.get();
            }
            testConfiguration.setSourcePath(script);
            if (BallerinaProjectUtils.isModuleTest(element)) {
                testConfiguration.addCommand("--tests");
                String source = packageName + "." + moduleName + ":"
                        + BallerinaPsiUtil.getFunctionName(element);
                testConfiguration.setSource(source);
            } else {
                testConfiguration.addCommand("--tests");
                String source = BallerinaPsiUtil.getFunctionName(element);
                testConfiguration.setSource(source);

            }
            try {
                if (isDebugging) {
                    ExecutionEnvironmentBuilder.create(project, DefaultRunExecutor.getRunExecutorInstance(),
                            testConfiguration).runner(new BallerinaDebugger()).buildAndExecute();
                } else {
                    ExecutionEnvironmentBuilder.create(project, DefaultRunExecutor.getRunExecutorInstance(),
                            testConfiguration).buildAndExecute();
                }
            } catch (ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (isDebugging) {
            e.getPresentation().setText("Debug " + "\"" + BallerinaPsiUtil.getFunctionName(element) + "\"");
        } else {
            e.getPresentation().setText("Test " + "\"" + BallerinaPsiUtil.getFunctionName(element) + "\"");
        }
    }
}
