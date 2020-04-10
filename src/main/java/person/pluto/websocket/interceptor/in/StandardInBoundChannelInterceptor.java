package person.pluto.websocket.interceptor.in;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import person.pluto.websocket.enumeration.IOReply;
import person.pluto.websocket.interceptor.in.process.IInBoundProcess;

/**
 * <p>
 * 消息入侧拦截
 * </p>
 *
 * @author Pluto
 * @since 2020-03-09 10:17:44
 */
@Component
public class StandardInBoundChannelInterceptor extends AbsInBoundChannelInterceptor {

    @Autowired
    private List<IInBoundProcess> processList;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // 哈哈哈我想起为啥这样写了，直接使用ChannelInterceptor的话，相当于只有true和false，
        // 所以拦截器它会一直走到最后一个，比如断开连接、用户合法性判断就会紊乱，所以要用这种三状态的模式
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        for (IInBoundProcess process : processList) {
            IOReply reply = process.process(accessor, message, channel);
            if (IOReply.DENY.equals(reply)) {
                return null;
            }
            if (IOReply.ACCEPT.equals(reply)) {
                return message;
            }
        }

        return message;
    }

}
