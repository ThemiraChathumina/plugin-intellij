package io.ballerina.plugins.idea.debugger.client;

import org.eclipse.lsp4j.debug.BreakpointEventArguments;
import org.eclipse.lsp4j.debug.CapabilitiesEventArguments;
import org.eclipse.lsp4j.debug.ContinuedEventArguments;
import org.eclipse.lsp4j.debug.ExitedEventArguments;
import org.eclipse.lsp4j.debug.LoadedSourceEventArguments;
import org.eclipse.lsp4j.debug.ModuleEventArguments;
import org.eclipse.lsp4j.debug.OutputEventArguments;
import org.eclipse.lsp4j.debug.ProcessEventArguments;
import org.eclipse.lsp4j.debug.StoppedEventArguments;
import org.eclipse.lsp4j.debug.TerminatedEventArguments;
import org.eclipse.lsp4j.debug.ThreadEventArguments;
import org.eclipse.lsp4j.debug.services.IDebugProtocolClient;

import static org.eclipse.lsp4j.debug.StoppedEventArgumentsReason.BREAKPOINT;
import static org.eclipse.lsp4j.debug.StoppedEventArgumentsReason.STEP;

/**
 * IDebugProtocolClient implementation.
 */
public class DAPClient implements IDebugProtocolClient {

    private DAPRequestManager requestManager;

    @Override
    public void initialized() {
    }

    @Override
    public void stopped(StoppedEventArguments args) {
        if (args.getReason().equals(BREAKPOINT) || args.getReason().equals(STEP)) {
            requestManager.getClientConnector().getContext().handleDebugHit(args);
        }
    }

    @Override
    public void continued(ContinuedEventArguments args) {
    }

    @Override
    public void exited(ExitedEventArguments args) {
        //Todo - Check for args
        requestManager.getClientConnector().getContext().stop();
    }

    @Override
    public void terminated(TerminatedEventArguments args) {
        requestManager.getClientConnector().getContext().stop();
    }

    @Override
    public void thread(ThreadEventArguments args) {
    }

    @Override
    public void output(OutputEventArguments args) {
    }

    @Override
    public void breakpoint(BreakpointEventArguments args) {
    }

    @Override
    public void module(ModuleEventArguments args) {
    }

    @Override
    public void loadedSource(LoadedSourceEventArguments args) {
    }

    @Override
    public void process(ProcessEventArguments args) {
    }

    @Override
    public void capabilities(CapabilitiesEventArguments args) {
    }

    public void connect(DAPRequestManager requestManager) {
        this.requestManager = requestManager;
    }

}
