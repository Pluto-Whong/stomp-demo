package person.pluto.websocket.interceptor;

import org.springframework.messaging.support.ChannelInterceptor;

/**
 * <p>
 * 消息进出口组合处理器
 * </p>
 *
 * @author Pluto
 * @since 2020-03-06 16:00:30
 */
public interface InOutBoundHandler {

    /**
     * 获取消息进口拦截器
     *
     * @return
     * @author Pluto
     * @since 2020-03-06 16:02:53
     */
    ChannelInterceptor[] getClientInboundInterceptors();

    /**
     * 获取消息出口拦截器
     *
     * @return
     * @author Pluto
     * @since 2020-03-06 16:06:16
     */
    ChannelInterceptor[] getClientOutboundInterceptors();

}
