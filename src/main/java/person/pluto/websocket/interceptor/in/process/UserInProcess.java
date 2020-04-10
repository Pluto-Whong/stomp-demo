package person.pluto.websocket.interceptor.in.process;

import java.security.Principal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import person.pluto.websocket.common.WebsocketFormat;
import person.pluto.websocket.enumeration.IOReply;
import person.pluto.websocket.model.WebsocketUserInfo;
import person.pluto.websocket.model.WebsocketUserInfoWrapper;
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
@Order(1000)
public class UserInProcess implements IInBoundProcess {

    @Autowired
    private WsuserServer wsuserServer;

    @Override
    public IOReply process(StompHeaderAccessor accessor, Message<?> message, MessageChannel channel) {
        // token检查 并设置用户

        Principal user = accessor.getUser();
        if (user == null || !(user instanceof WebsocketUserInfo)) {
            log.error("websocket发来请求，但是通道未注册用户[{}]", message);
            return IOReply.DENY;
        }

        String terminalType = accessor.getFirstNativeHeader(WebsocketFormat.TERMINAL_TYPE_KEY);
        String token = accessor.getFirstNativeHeader(WebsocketFormat.TOKEN_HEADER_KEY);
        if (StringUtils.isBlank(token)) {
            log.warn("websocket发来请求，无token [{}]", message);
            return IOReply.DENY;
        }

        String userId = null;
        try {
            userId = wsuserServer.inspectToken(token);
        } catch (Exception e) {
            log.warn("websocket发来请求， token解析异常 [{}]", message);
            return IOReply.DENY;
        }

        WebsocketUserInfoWrapper wrapper = WebsocketUserInfoWrapper.of(userId, terminalType);
        WebsocketUserInfo cacheUserInfo = wsuserServer.getUserInfo(wrapper);

        WebsocketUserInfo wsUserInfo = (WebsocketUserInfo) user;

        if (cacheUserInfo == null || !cacheUserInfo.sameUser(wsUserInfo)) {
            log.error("websocket发来请求，有其他人或旧登陆[{}]复用了该通道 [{}]", userId, wsUserInfo);
            // 发送消息通知接触链路
            return IOReply.DENY;
        }

        return IOReply.NEUTRAL;
    }

}
