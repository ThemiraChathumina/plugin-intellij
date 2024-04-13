package org.intellij.sdk.language.debugger.breakpoint;

import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import org.jetbrains.annotations.Nullable;

/**
 * Represent a Ballerina breakpoint.
 */
public class BallerinaBreakpointProperties extends XBreakpointProperties<BallerinaBreakpointProperties> {

    @Nullable
    @Override
    public BallerinaBreakpointProperties getState() {
        return this;
    }

    @Override
    public void loadState(BallerinaBreakpointProperties state) {
    }
}

