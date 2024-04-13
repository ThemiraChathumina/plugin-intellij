package org.intellij.sdk.language.debugger;


import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a stack of execution frames usually corresponding to a thread. It is shown in 'Frames' panel of
 * 'Debug' tool window.
 */
public class BallerinaExecutionStack extends XExecutionStack {

    private final Long myWorkerID;
    @NotNull
    private final BallerinaDebugProcess myProcess;
    @NotNull
    private final List<BallerinaStackFrame> myStacks;

    private final BallerinaSuspendContext myContext;

    BallerinaExecutionStack(@NotNull BallerinaDebugProcess process, BallerinaSuspendContext context, Long myWorkerID,
                            @NotNull List<BallerinaStackFrame> frames) {
        super(" Worker #" + myWorkerID);
        this.myWorkerID = myWorkerID;
        this.myContext = context;
        this.myProcess = process;
        this.myStacks = frames;
    }

    @Nullable
    @Override
    public XStackFrame getTopFrame() {
        return ContainerUtil.getFirstItem(myStacks);
    }

    @Override
    public void computeStackFrames(int firstFrameIndex, @NotNull XStackFrameContainer container) {
        // Note - Need to add an empty list if the index is not 0. Otherwise will not work properly.
        if (firstFrameIndex == 0) {
            container.addStackFrames(myStacks, true);
        } else {
            container.addStackFrames(new LinkedList<>(), true);
        }
        myContext.setMyActiveStack(this);
    }

    Long getMyWorkerID() {
        return myWorkerID;
    }
}
