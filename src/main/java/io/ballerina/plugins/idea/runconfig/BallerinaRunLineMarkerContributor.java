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

package io.ballerina.plugins.idea.runconfig;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.PsiElement;
import io.ballerina.plugins.idea.BallerinaIcons;
import io.ballerina.plugins.idea.project.BallerinaProjectUtils;
import io.ballerina.plugins.idea.psi.BallerinaPsiUtil;
import io.ballerina.plugins.idea.runconfig.application.BallerinaApplicationDebugAction;
import io.ballerina.plugins.idea.runconfig.application.BallerinaRunFileAction;
import io.ballerina.plugins.idea.runconfig.test.BallerinaTestFunctionAction;
import io.ballerina.plugins.idea.sdk.BallerinaSdkService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Add gutter icons for running and debugging Ballerina files.
 *
 * @since 2.0.0
 */
public class BallerinaRunLineMarkerContributor extends RunLineMarkerContributor {

    @Nullable
    @Override
    public Info getInfo(@NotNull PsiElement psiElement) {
        String version = BallerinaSdkService.getInstance().getBallerinaVersion(psiElement.getProject());
        if (version != null && BallerinaPsiUtil.isMainFunction(psiElement) || BallerinaPsiUtil.isService(psiElement)) {
            AnAction[] actions = new AnAction[2];
            actions[0] = new BallerinaRunFileAction();
            actions[1] = new BallerinaApplicationDebugAction();
            return new Info(BallerinaIcons.RUN, actions, null);
        }
        if (version != null && BallerinaPsiUtil.isTestFunction(psiElement) &&
                (BallerinaProjectUtils.isPackageTest(psiElement) || BallerinaProjectUtils.isModuleTest(psiElement))) {
            AnAction[] actions = new AnAction[2];
            actions[0] = new BallerinaTestFunctionAction(psiElement, false);
            actions[1] = new BallerinaTestFunctionAction(psiElement, true);
            return new Info(BallerinaIcons.TEST, actions, null);
        }
        return null;
    }
}
