package person.pluto.websocket.interceptor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.ChannelInterceptor;

import person.pluto.websocket.interceptor.in.AbsInBoundChannelInterceptor;
import person.pluto.websocket.interceptor.out.AbsOutBoundChannelInterceptor;

/**
 * <p>
 * 消息进出口组合处理器
 * </p>
 *
 * @author Pluto
 * @since 2020-03-06 16:17:23
 */
public class SimpleInOutBoundHandler implements InOutBoundHandler {

    private List<AbsInBoundChannelInterceptor> clientInboundInterceptors;
    private List<AbsOutBoundChannelInterceptor> clientOutboundInterceptors;

    @Override
    public ChannelInterceptor[] getClientInboundInterceptors() {
        if (clientInboundInterceptors == null || clientInboundInterceptors.size() < 1) {
            return null;
        }
        return clientInboundInterceptors.toArray(new ChannelInterceptor[clientInboundInterceptors.size()]);
    }

    @Override
    public ChannelInterceptor[] getClientOutboundInterceptors() {
        if (clientOutboundInterceptors == null || clientOutboundInterceptors.size() < 1) {
            return null;
        }
        return clientOutboundInterceptors.toArray(new ChannelInterceptor[clientOutboundInterceptors.size()]);
    }

    @Autowired(required = false)
    public void setClientInboundInterceptors(List<AbsInBoundChannelInterceptor> clientInboundInterceptors) {
        if (clientInboundInterceptors == null) {
            return;
        }
        for (AbsInBoundChannelInterceptor model : clientInboundInterceptors) {
            model.setInOutBoundHandler(this);
        }
        this.clientInboundInterceptors = clientInboundInterceptors;
    }

    @Autowired(required = false)
    public void setClientOutboundInterceptors(List<AbsOutBoundChannelInterceptor> clientOutboundInterceptors) {
        if (clientOutboundInterceptors == null) {
            return;
        }
        for (AbsOutBoundChannelInterceptor model : clientOutboundInterceptors) {
            model.setInOutBoundHandler(this);
        }
        this.clientOutboundInterceptors = clientOutboundInterceptors;
    }

}
