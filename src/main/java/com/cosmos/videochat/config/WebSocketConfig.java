package com.cosmos.videochat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private WebSocketHeaderInterceptor webSocketHeaderInterceptor;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketConfig(WebSocketHeaderInterceptor webSocketHeaderInterceptor, SimpMessagingTemplate simpMessagingTemplate) {
        this.webSocketHeaderInterceptor = webSocketHeaderInterceptor;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(simpMessagingTemplate), "/socket")
                .addInterceptors(webSocketHeaderInterceptor)
                .setAllowedOrigins("*", "http://localhost:4200", "https://system.cosmosastrology.com");
    }








}
