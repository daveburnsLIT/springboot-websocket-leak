package org.sample.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;


@Configuration
@EnableWebSocketMessageBroker
public class RabbitMQWebsocketBrokerConfiguration extends AbstractWebSocketMessageBrokerConfigurer {
   
    
    private static final String STOMP_TOPIC = "/topic";
    private static final String STOMP_EXCHANGE = "/exchange";
    private static final String APP_PREFIX = "/app";
    private static final String EXCHANGE_PREFIX = "/exchange";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        
        LOGGER.debug("Setting up Websocket Broker");
                
        config.enableStompBrokerRelay(STOMP_TOPIC, STOMP_EXCHANGE)
        .setUserRegistryBroadcast("/topic/simp-user-registry");
        
        config.setApplicationDestinationPrefixes(APP_PREFIX, EXCHANGE_PREFIX);
    }

    /**
     * JS Clients register at this stomp endpoint, it is an authenticated 
     * endpoint.
     * 
     * @param registry  The stomp registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        
        registry.addEndpoint("/websocket").withSockJS();
    }
}

