package org.intellij.sdk.language.debugger.client;


import com.intellij.openapi.diagnostic.Logger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import org.intellij.sdk.language.debugger.Callback;

/**
 * WebSocket client handler which handles communication with the debug server.
 */
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOGGER = Logger.getInstance(WebSocketClientHandler.class);

    private final WebSocketClientHandshaker handshaker;
    private final Callback callback;
    private ChannelPromise handshakeFuture;
    private boolean isConnected;

    WebSocketClientHandler(WebSocketClientHandshaker handshaker, Callback callback) {
        this.handshaker = handshaker;
        this.callback = callback;
    }

    ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
        isConnected = true;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.debug("WebSocket Client disconnected!");
        isConnected = false;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            LOGGER.debug("WebSocket Client connected!");
            handshakeFuture.setSuccess();
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content()
                            .toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            LOGGER.debug("WebSocket Client received text message: " + textFrame.text());
            String textReceived = textFrame.text();
            callback.call(textReceived);
        } else if (frame instanceof CloseWebSocketFrame) {
            isConnected = false;
            LOGGER.debug("WebSocket Client received closing");
            ch.close();
        }
    }

    boolean isConnected() {
        return isConnected;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!handshakeFuture.isDone()) {
            LOGGER.debug("Handshake failed : " + cause.getMessage(), cause);
            handshakeFuture.setFailure(cause);
            isConnected = false;
        }
        ctx.close();
    }
}
