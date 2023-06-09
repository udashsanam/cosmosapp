package com.cosmos.videochat.config1;

import com.cosmos.videochat.util.VideoChatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final AuthenticationHandler authenticationHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // Set prefixes for the endpoint that the client listens for our messages from
        registry.enableSimpleBroker("/topic");
        // Set prefix for endpoints the client will send messages to
        registry.setApplicationDestinationPrefixes("/app");

    }


    /**
     * <p>
     *     adds the endpoint "/stomp" along with allowed origins.
     * </p>
     * @param registry an object of <code>StompEndpointRegistry</code> class
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // Registers the endpoint where the handshake will take place
        registry.addEndpoint("/api/stomp").setAllowedOrigins(VideoChatUtil.ALLOWED_ORIGINS).withSockJS();

    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        // Add our interceptor for authentication/authorization
        registration.interceptors(authenticationHandler);

    }
}
