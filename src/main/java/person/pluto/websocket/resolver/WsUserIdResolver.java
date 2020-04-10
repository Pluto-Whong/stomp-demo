package person.pluto.websocket.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import person.pluto.interactive.annotation.CurrentUserId;
import person.pluto.websocket.model.WebsocketUserInfo;

/**
 * <p>
 * websocket用户ID注入
 * </p>
 *
 * @author Pluto
 * @since 2020-03-09 11:51:56
 */
public class WsUserIdResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class)
                && parameter.getParameterType().isAssignableFrom(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        WebsocketUserInfo wsUserInfo = (WebsocketUserInfo) accessor.getUser();
        return wsUserInfo.getUserId();
    }

}
