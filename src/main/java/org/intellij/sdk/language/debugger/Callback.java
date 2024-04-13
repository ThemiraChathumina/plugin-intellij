package org.intellij.sdk.language.debugger;

import org.intellij.sdk.language.debugger.client.WebSocketClientHandler;

/**
 * This is used to pass the callback function to
 * {@link WebSocketClientHandler} class.
 */
@FunctionalInterface
public interface Callback {

    void call(String response);
}
