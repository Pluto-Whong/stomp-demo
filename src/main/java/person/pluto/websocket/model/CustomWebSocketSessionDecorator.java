package person.pluto.websocket.model;

import java.io.IOException;
import java.security.Principal;

import person.pluto.websocket.handler.CustomSubProtocolWebSocketHandler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

/**
 * 
 * <p>
 * 自定义的websocket控制器
 * </p>
 *
 * @author Pluto
 * @since 2020-04-07 10:21:44
 */
public class CustomWebSocketSessionDecorator extends ConcurrentWebSocketSessionDecorator {

    protected CustomSubProtocolWebSocketHandler webSocketHandler;

    protected Principal user;

    public CustomWebSocketSessionDecorator(WebSocketSession delegate, int sendTimeLimit, int bufferSizeLimit,
            CustomSubProtocolWebSocketHandler webSocketHandler) {
        super(delegate, sendTimeLimit, bufferSizeLimit);
        this.webSocketHandler = webSocketHandler;
    }

    public CustomWebSocketSessionDecorator(WebSocketSession delegate, int sendTimeLimit, int bufferSizeLimit,
            OverflowStrategy overflowStrategy, CustomSubProtocolWebSocketHandler webSocketHandler) {
        super(delegate, sendTimeLimit, bufferSizeLimit, overflowStrategy);
        this.webSocketHandler = webSocketHandler;
    }

    public void setPrincipal(Principal user) {
        this.user = user;
    }

    @Override
    public Principal getPrincipal() {
        return this.user;
    }

    @Override
    public void close() throws IOException {
        this.close(CloseStatus.GOING_AWAY);
    }

    @Override
    public void close(CloseStatus status) throws IOException {
        super.close(status);
        webSocketHandler.sessionClear(this, status);
    }

}