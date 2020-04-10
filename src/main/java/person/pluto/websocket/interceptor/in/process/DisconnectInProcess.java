package person.pluto.websocket.interceptor.in.process;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import person.pluto.websocket.enumeration.IOReply;
import person.pluto.websocket.model.WebsocketUserInfo;
import person.pluto.websocket.server.WsuserServer;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 用户验证
 * </p>
 *
 * @author Pluto
 * @since 2020-03-09 13:34:54
 */
@Slf4j
@Component
@Order(500)
public class DisconnectInProcess implements IInBoundProcess {

    @Autowired
    private WsuserServer wsuserServer;

    @Override
    public IOReply process(StompHeaderAccessor accessor, Message<?> message, MessageChannel channel) {
        if (!StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            return IOReply.NEUTRAL;
        }

        // 清理缓存中用户信息
        Principal user = accessor.getUser();
        if (user == null || !(user instanceof WebsocketUserInfo)) {
            log.warn("websocket发来Disconnect通知，但是用户模型无法解析[{}]", message);
            return IOReply.ACCEPT;
        }

        WebsocketUserInfo wsUserInfo = (WebsocketUserInfo) user;
        wsuserServer.removeUserInfo(wsUserInfo);

        return IOReply.ACCEPT;
    }

}
