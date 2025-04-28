package com.retriage.retriage.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for setting up WebSocket message handling using STOMP.
 * This enables a message broker and defines endpoints for clients to connect to.
 */
@Configuration
@EnableWebSocketMessageBroker
public class EventWebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    /**
     * Registers STOMP endpoints that clients can use to connect to the WebSocket server.
     * The "/active_event" endpoint is exposed for WebSocket connections.
     *
     * @param registry The registry for adding and configuring STOMP endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/active_event");
    }

    /**
     * Configures the message broker for handling messages sent to and from clients.
     * Enables a simple in-memory broker to send messages to destinations prefixed with "/topic".
     * Sets the application destination prefix to "/ws" for messages sent from clients to the server.
     * @param registry The registry for configuring the message broker.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/ws");
    }
}