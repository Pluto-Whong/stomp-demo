package person.pluto.websocket.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import person.pluto.websocket.interceptor.InOutBoundHandler;
import person.pluto.websocket.interceptor.SimpleInOutBoundHandler;
import person.pluto.websocket.resolver.WsUserIdResolver;
import person.pluto.websocket.resolver.WsUserResolver;

/**
 * <p>
 * websocket配置类，使用sockjs通信，协议栈使用stomp
 * </p>
 *
 * @author Pluto
 * @since 2020-03-06 15:12:15
 */
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Bean
    public InOutBoundHandler getInOutBoundHandler() {
        return new SimpleInOutBoundHandler();
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/im/ws").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 客户端向服务端发送消息需有/app 前缀
        // registry.setApplicationDestinationPrefixes("/app");
        // 指定用户发送（一对一）的前缀 /user/
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(getWsUserIdResolver());
        argumentResolvers.add(getWsUserResolver());
    }

    @Bean
    public WsUserIdResolver getWsUserIdResolver() {
        return new WsUserIdResolver();
    }

    @Bean
    public WsUserResolver getWsUserResolver() {
        return new WsUserResolver();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        InOutBoundHandler inOutBoundHandler = getInOutBoundHandler();
        if (inOutBoundHandler != null) {
            ChannelInterceptor[] clientInboundInterceptors = inOutBoundHandler.getClientInboundInterceptors();
            if (clientInboundInterceptors != null && clientInboundInterceptors.length > 0) {
                registration.interceptors(clientInboundInterceptors);
            }
        }
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        InOutBoundHandler inOutBoundHandler = getInOutBoundHandler();
        if (inOutBoundHandler != null) {
            ChannelInterceptor[] clientOutboundInterceptors = inOutBoundHandler.getClientOutboundInterceptors();
            if (clientOutboundInterceptors != null && clientOutboundInterceptors.length > 0) {
                registration.interceptors(clientOutboundInterceptors);
            }
        }
    }

}
