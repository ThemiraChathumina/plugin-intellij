package org.intellij.sdk.language.debugger.client.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Declaration of stream connection provider functionality.
 */
public interface BallerinaStreamConnectionProvider {

    void start() throws IOException;

    InputStream getInputStream();

    OutputStream getOutputStream();

    void stop();

}
