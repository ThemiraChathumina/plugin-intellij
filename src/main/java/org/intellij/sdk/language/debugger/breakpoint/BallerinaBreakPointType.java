package org.intellij.sdk.language.debugger.breakpoint;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import org.intellij.sdk.language.BallerinaFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a Ballerina breakpoint type.
 */
public class BallerinaBreakPointType extends XLineBreakpointType<BallerinaBreakpointProperties> {

    public static final String ID = "BallerinaLineBreakpoint";
    public static final String NAME = "Ballerina breakpoint";
    private static final String BALLERINA_LINE_COMMENT_PREFIX = "//";

    protected BallerinaBreakPointType() {
        super(ID, NAME);
    }

    @Nullable
    @Override
    public BallerinaBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
        return new BallerinaBreakpointProperties();
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        return line >= 0 && file.getFileType() == BallerinaFileType.INSTANCE && isLineBreakpointAvailable(file, line,
                project);
    }

    private static boolean isLineBreakpointAvailable(@NotNull VirtualFile file, int line, @NotNull Project project) {
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document == null || document.getLineEndOffset(line) == document.getLineStartOffset(line)) {
            return false;
        }
        Checker canPutAtChecker = new Checker();
        XDebuggerUtil.getInstance().iterateLine(project, document, line, canPutAtChecker);

        // Used to identify line comments, which cannot be correctly processed by the checker.
        if (document.getText(new TextRange(document.getLineStartOffset(line), document.getLineEndOffset(line))).trim()
                .startsWith(BALLERINA_LINE_COMMENT_PREFIX)) {
            return false;
        }

        return canPutAtChecker.isLineBreakpointAvailable();
    }

    private static final class Checker implements Processor<PsiElement> {

        private boolean myIsLineBreakpointAvailable;

        @Override
        public boolean process(@NotNull PsiElement element) {
            IElementType type = element.getNode().getElementType();
            myIsLineBreakpointAvailable = !(type instanceof PsiWhiteSpace) && !element.getNode().getText().isEmpty();
            return true;
        }

        boolean isLineBreakpointAvailable() {
            return myIsLineBreakpointAvailable;
        }
    }
}
