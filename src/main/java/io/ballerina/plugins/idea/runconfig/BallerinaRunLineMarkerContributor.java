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

public class BallerinaRunLineMarkerContributor extends RunLineMarkerContributor {

    @Nullable
    @Override
    public Info getInfo(@NotNull PsiElement psiElement) {
        String version = BallerinaSdkService.getInstance().getBallerinaVersion(psiElement.getProject());
        if (version != null && BallerinaPsiUtil.isMainFunction(psiElement) || BallerinaPsiUtil.isService(psiElement)) {
            AnAction [] actions = new AnAction[2];
            actions[0] = new BallerinaRunFileAction();
            actions[1] = new BallerinaApplicationDebugAction();
            return new Info(BallerinaIcons.RUN,actions,null);
        }
        if (version != null && BallerinaPsiUtil.isTestFunction(psiElement)
                && (BallerinaProjectUtils.isPackageTest(psiElement) || BallerinaProjectUtils.isModuleTest(psiElement))) {
            AnAction [] actions = new AnAction[2];
            actions[0] = new BallerinaTestFunctionAction(psiElement, false);
            actions[1] = new BallerinaTestFunctionAction(psiElement, true);
            return new Info(BallerinaIcons.TEST,actions,null);
        }
        return null;
    }

}
