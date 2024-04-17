package io.ballerina.plugins.idea.debugger;

import io.ballerina.plugins.idea.debugger.client.WebSocketClientHandler;

/**
 * This is used to pass the callback function to
 * {@link WebSocketClientHandler} class.
 */
@FunctionalInterface
public interface Callback {

    void call(String response);
}
