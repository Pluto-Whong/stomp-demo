package person.pluto.websocket.interceptor.in.process;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import person.pluto.websocket.enumeration.IOReply;

/**
 * <p>
 * 消息入侧处理过程
 * </p>
 *
 * @author Pluto
 * @since 2020-03-09 13:20:25
 */
public interface IInBoundProcess {

    /**
     * 处理，并判断是否需要继续
     *
     * @param accessor
     * @param message
     * @param channel
     * @return
     * @author Pluto
     * @since 2020-03-09 13:25:01
     */
    IOReply process(StompHeaderAccessor accessor, Message<?> message, MessageChannel channel);

}
