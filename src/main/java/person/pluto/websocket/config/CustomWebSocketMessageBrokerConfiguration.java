package person.pluto.websocket.config;

import person.pluto.websocket.handler.CustomSubProtocolWebSocketHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.DelegatingWebSocketMessageBrokerConfiguration;

/**
 * 
 * <p>
 * 自定义的websocket配置类
 * </p>
 *
 * @author Pluto
 * @since 2020-04-07 08:42:40
 */
@Configuration
public class CustomWebSocketMessageBrokerConfiguration extends DelegatingWebSocketMessageBrokerConfiguration {

    @Bean
    public CustomSubProtocolWebSocketHandler customSubProtocolWebSocketHandler() {
        return new CustomSubProtocolWebSocketHandler(clientInboundChannel(), clientOutboundChannel());
    }

    @Override
    public WebSocketHandler subProtocolWebSocketHandler() {
        return customSubProtocolWebSocketHandler();
    }

}