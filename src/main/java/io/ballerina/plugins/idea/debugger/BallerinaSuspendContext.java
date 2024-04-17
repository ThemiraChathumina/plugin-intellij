package io.ballerina.plugins.idea.debugger;

import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import org.eclipse.lsp4j.debug.StackFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.eclipse.xtext.xbase.lib.StringExtensions.isNullOrEmpty;

/**
 * Represent a Ballerina suspended context. Created in debug hits.
 */
public class BallerinaSuspendContext extends XSuspendContext {

    private final BallerinaDebugProcess myProcess;
    private final List<BallerinaExecutionStack> myExecutionStacks = new LinkedList<>();
    private BallerinaExecutionStack myActiveStack;

    BallerinaSuspendContext(@NotNull BallerinaDebugProcess process) {
        myProcess = process;
    }

    void addToExecutionStack(Long threadId, StackFrame[] stackFrames) {
        List<BallerinaStackFrame> balStackFrames = toBalStackFrames(stackFrames);
        BallerinaExecutionStack stack = new BallerinaExecutionStack(myProcess, this, threadId, balStackFrames);
        myExecutionStacks.add(stack);
        myActiveStack = stack;
    }

    @Nullable
    @Override
    public BallerinaExecutionStack getActiveExecutionStack() {
        return myActiveStack;
    }

    void setMyActiveStack(BallerinaExecutionStack stack) {
        myActiveStack = stack;
    }

    @NotNull
    @Override
    public XExecutionStack[] getExecutionStacks() {
        return myExecutionStacks.toArray(new BallerinaExecutionStack[0]);
    }

    private List<BallerinaStackFrame> toBalStackFrames(StackFrame[] frames) {
        List<BallerinaStackFrame> balStackFrames = new ArrayList<>();
        for (StackFrame frame : frames) {
            // Todo - Enable java stack frames
            if (isBallerinaSource(frame)) {
                balStackFrames.add(new BallerinaStackFrame(myProcess, frame));
            }
        }
        return balStackFrames;
    }

    private boolean isBallerinaSource(StackFrame frame) {

        if (frame == null) {
            return false;
        }

        String fileName = frame.getSource().getName();
        String filePath = frame.getSource().getPath();
        if (frame.getSource() == null || isNullOrEmpty(fileName) || isNullOrEmpty(filePath)) {
            return false;
        }

        if (fileName.split("\\.").length <= 1) {
            return false;
        }

        return fileName.endsWith("bal");
    }
}
