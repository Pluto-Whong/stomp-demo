package person.pluto.websocket.interceptor.in.process;

import java.security.Principal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import person.pluto.lock.actuator.LockActuator;
import person.pluto.websocket.common.WebsocketFormat;
import person.pluto.websocket.enumeration.IOReply;
import person.pluto.websocket.handler.CustomSubProtocolWebSocketHandler;
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
@Order(400)
public class ConnectInProcess implements IInBoundProcess {

    @Autowired
    private WsuserServer wsuserServer;

//    @Autowired
//    private CommonRabbitServer commonRabbitServer;

    @Autowired
    private CustomSubProtocolWebSocketHandler customSubProtocolWebSocketHandler;

    @Override
    public IOReply process(StompHeaderAccessor accessor, Message<?> message, MessageChannel channel) {
        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            return IOReply.NEUTRAL;
        }

        Principal user = accessor.getUser();
        if (user != null) {
            log.warn("websocket发来connect通知，但是已存在用户模型[{}]，不允许重复注册连接", message);
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

        WebsocketUserInfo websocketUserInfo = WebsocketUserInfo.ofFull(userId, terminalType, accessor.getSessionId());

        // 为避免死锁，将该操作看作事务，提前进行行级锁，并在事务完成后进行解锁
        String userUnoinAddress = WebsocketFormat.userUnoinAddress(websocketUserInfo);
        Assert.isTrue(LockActuator.trylock(userUnoinAddress), String.format("锁定[%s]失败", userUnoinAddress));
        try {
            wsuserServer.setUserInfo(websocketUserInfo);
            accessor.setUser(websocketUserInfo);
            customSubProtocolWebSocketHandler.setUserInfo(accessor.getSessionId(), websocketUserInfo);
            // 通知清理其他的链路
//            commonRabbitServer.notifyClearUser(websocketUserInfo);
            customSubProtocolWebSocketHandler.closeByUserInfo(websocketUserInfo);
        } finally {
            LockActuator.releaseLock(userUnoinAddress);
        }

        return IOReply.ACCEPT;
    }

}
