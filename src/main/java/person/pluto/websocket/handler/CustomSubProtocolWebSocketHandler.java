package person.pluto.websocket.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import person.pluto.lock.actuator.LockActuator;
import person.pluto.websocket.common.WebsocketFormat;
import person.pluto.websocket.model.CustomWebSocketSessionDecorator;
import person.pluto.websocket.model.WebsocketUserInfo;
import person.pluto.websocket.model.WebsocketUserInfoWrapper;
import person.pluto.websocket.server.WsuserServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

/**
 * 
 * <p>
 * 自定义的websocket通道处理器
 * </p>
 *
 * @author Pluto
 * @since 2020-04-07 08:45:49
 */
public class CustomSubProtocolWebSocketHandler extends SubProtocolWebSocketHandler {

    protected Map<String, Set<String>> userSessionIdMap = new HashMap<>();
    protected Map<String, CustomWebSocketSessionDecorator> sessionMap = new HashMap<>();

    private final Lock mapLock = new ReentrantLock();

    @Autowired
    private WsuserServer wsuserServer;

    public CustomSubProtocolWebSocketHandler(MessageChannel clientInboundChannel,
            SubscribableChannel clientOutboundChannel) {
        super(clientInboundChannel, clientOutboundChannel);
    }

    @Override
    protected WebSocketSession decorateSession(WebSocketSession session) {
        CustomWebSocketSessionDecorator decorator = new CustomWebSocketSessionDecorator(session, getSendTimeLimit(),
                getSendBufferSizeLimit(), this);
        sessionMap.put(session.getId(), decorator);
        return decorator;
    }

    /**
     * 设置用户信息
     * 
     * @param sessionId
     * @param userInfo
     * @author Pluto
     * @since 2020-04-07 11:58:37
     */
    public void setUserInfo(String sessionId, WebsocketUserInfo userInfo) {
        String userUnoinAddress = WebsocketFormat.userUnoinAddress(userInfo);
        mapLock.lock();
        try {
            CustomWebSocketSessionDecorator decorator = sessionMap.get(sessionId);
            Assert.notNull(decorator, String.format("sessionId [%s] 的decorator为空", sessionId));

            decorator.setPrincipal(userInfo);

            Set<String> set = userSessionIdMap.get(userUnoinAddress);
            if (set == null) {
                set = new HashSet<>();
                userSessionIdMap.put(userUnoinAddress, set);
            }
            set.add(sessionId);
        } finally {
            mapLock.unlock();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        super.afterConnectionClosed(session, closeStatus);
        this.sessionClear(session, closeStatus);
    }

    /**
     * 清理session
     * 
     * @param session
     * @param closeStatus
     * @author Pluto
     * @since 2020-04-07 11:58:13
     */
    public void sessionClear(WebSocketSession session, CloseStatus closeStatus) {
        // 关闭websocket session
        mapLock.lock();
        try {
            CustomWebSocketSessionDecorator remove = sessionMap.remove(session.getId());
            if (remove != null) {
                WebsocketUserInfo userInfo = (WebsocketUserInfo) remove.getPrincipal();
                if (userInfo != null) {
                    String userUnoinAddress = WebsocketFormat.userUnoinAddress(userInfo);
                    Set<String> set = userSessionIdMap.get(userUnoinAddress);
                    if (set != null) {
                        set.remove(session.getId());
                        if (set.isEmpty()) {
                            userSessionIdMap.remove(userUnoinAddress);
                        }
                    }
                }
            }
        } finally {
            mapLock.unlock();
        }
    }

    /**
     * 关闭指定用户
     * 
     * @param userInfo
     * @author Pluto
     * @since 2020-04-07 14:12:22
     */
    public void closeByUserInfo(WebsocketUserInfo userInfo) {
        List<WebSocketSession> sessionList = new LinkedList<>();

        String userUnoinAddress = WebsocketFormat.userUnoinAddress(userInfo);

        Assert.isTrue(LockActuator.trylock(userUnoinAddress), String.format("锁定[%s]失败", userUnoinAddress));

        try {
            WebsocketUserInfoWrapper wrapper = WebsocketUserInfoWrapper.of(userInfo);
            WebsocketUserInfo cacheUserInfo = wsuserServer.getUserInfo(wrapper);

            mapLock.lock();
            try {
                Set<String> set = userSessionIdMap.get(userUnoinAddress);
                if (set != null) {
                    Iterator<String> iterator = set.iterator();
                    while (iterator.hasNext()) {
                        String next = iterator.next();

                        CustomWebSocketSessionDecorator customWebSocketSessionDecorator = sessionMap.get(next);
                        if (customWebSocketSessionDecorator == null) {
                            // 此处是将那些无关联的进行清理，防止内存泄漏，从目前逻辑上来看应该不会发生，做个保险
                            iterator.remove();
                            continue;
                        }
                        WebsocketUserInfo tempUserInfo = (WebsocketUserInfo) customWebSocketSessionDecorator
                                .getPrincipal();
                        if (tempUserInfo == null || !tempUserInfo.sameUser(cacheUserInfo)) {
                            // 此处只判断哪些需要关闭，不清理sessionMap和userSessionIdMap，交给close逻辑处理
                            sessionList.add(customWebSocketSessionDecorator);
                            continue;
                        }
                    }

                    if (set.isEmpty()) {
                        userSessionIdMap.remove(userUnoinAddress);
                    }
                }
            } finally {
                mapLock.unlock();
            }
        } finally {
            LockActuator.releaseLock(userUnoinAddress);
        }

        for (WebSocketSession session : sessionList) {
            try {
                session.close();
            } catch (Exception e) {
                // do nothing
            }
        }

    }

}