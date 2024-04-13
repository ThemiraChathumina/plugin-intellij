package org.intellij.sdk.language.debugger;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Responsible for handling console history.
 */
public class BallerinaHistoryProcessListener extends ProcessAdapter {

    private final ConcurrentLinkedQueue<Pair<ProcessEvent, Key>> myHistory = new ConcurrentLinkedQueue<>();

    @Override
    public void onTextAvailable(ProcessEvent event, Key outputType) {
        myHistory.add(Pair.create(event, outputType));
    }

    public void apply(ProcessHandler listener) {
        for (Pair<ProcessEvent, Key> pair : myHistory) {
            listener.notifyTextAvailable(pair.getFirst().getText(), pair.getSecond());
        }
    }
}

