package person.pluto.websocket.interceptor.in.process;

import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import person.pluto.websocket.enumeration.IOReply;
import person.pluto.websocket.model.WebsocketUserInfo;

/**
 * <p>
 * 用户验证
 * </p>
 *
 * @author Pluto
 * @since 2020-03-09 13:34:54
 */
@Component
public class SubscribeInProcess implements IInBoundProcess {

    @Override
    public IOReply process(StompHeaderAccessor accessor, Message<?> message, MessageChannel channel) {
        if (!StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            return IOReply.NEUTRAL;
        }

        WebsocketUserInfo wsUserInfo = (WebsocketUserInfo) accessor.getUser();

        String destination = accessor.getDestination();

        if (!StringUtils.equals(destination, String.format("/user/%s/msg", wsUserInfo.getUserId()))) {
            return IOReply.DENY;
        }

        return IOReply.NEUTRAL;
    }

}
