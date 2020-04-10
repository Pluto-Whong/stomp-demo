package person.pluto.websocket.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.Assert;

import person.pluto.interactive.annotation.CurrentUser;
import person.pluto.user.model.IMUserInfo;
import person.pluto.websocket.model.WebsocketUserInfo;
import person.pluto.websocket.server.WsuserServer;

/**
 * <p>
 * websocket用户信息注入
 * </p>
 *
 * @author Pluto
 * @since 2020-03-09 11:51:56
 */
public class WsUserResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private WsuserServer wsuserServer;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().isAssignableFrom(IMUserInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        WebsocketUserInfo websocketUserInfo = (WebsocketUserInfo) accessor.getUser();
        IMUserInfo userInfo = wsuserServer.getIMUserInfoByUserId(websocketUserInfo.getUserId());
        Assert.notNull(userInfo, String.format("用户[userId = %s]信息查询不到", websocketUserInfo.getUserId()));
        return userInfo;
    }

}
