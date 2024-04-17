package io.ballerina.plugins.idea.debugger;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.EvaluationMode;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import io.ballerina.plugins.idea.BallerinaFileType;
import io.ballerina.plugins.idea.debugger.evaluator.BallerinaExpressionCodeFragment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Ballerina debug editor provider.
 */
public class BallerinaDebuggerEditorsProvider extends XDebuggerEditorsProvider {

    private static final Logger LOG = Logger.getInstance(BallerinaDebuggerEditorsProvider.class);

    @NotNull
    @Override
    public FileType getFileType() {
        return BallerinaFileType.INSTANCE;
    }

    @NotNull
    @Override
    public Document createDocument(@NotNull Project project, @NotNull String text,
                                   @Nullable XSourcePosition sourcePosition, @NotNull EvaluationMode mode) {

        LOG.debug(String.format("createDocument  %s %s", mode, text));
        BallerinaExpressionCodeFragment codeFragment = new BallerinaExpressionCodeFragment(project, text);
        return Objects.requireNonNull(PsiDocumentManager.getInstance(project).getDocument(codeFragment));
    }
}
