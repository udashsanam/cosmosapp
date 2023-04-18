package com.cosmos.videochat.controller;

import com.cosmos.videochat.config.SocketHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController()
public class MyController {


//        @GetMapping("/api/getactiveusers")
//    public ResponseEntity<?> getActiveUsers(){
//            Map<String, WebSocketSession> sessions = SocketHandler.sessions;
//
//
//
//        return ResponseEntity.ok(appUserDtos);
//
//    }




}
