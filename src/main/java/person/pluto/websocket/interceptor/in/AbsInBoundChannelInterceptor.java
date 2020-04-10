package person.pluto.websocket.interceptor.in;

import org.springframework.messaging.support.ChannelInterceptor;

import person.pluto.websocket.interceptor.InOutBoundHandler;

/**
 * <p>
 * 通道拦截器
 * </p>
 *
 * @author Pluto
 * @since 2020-03-06 17:19:38
 */
public abstract class AbsInBoundChannelInterceptor implements ChannelInterceptor {

    protected InOutBoundHandler handler;

    /**
     * 设置管理类
     *
     * @param handler
     * @author Pluto
     * @since 2020-03-06 17:20:38
     */
    public void setInOutBoundHandler(InOutBoundHandler handler) {
        this.handler = handler;
    }

}
