package com.cosmos.videochat.controller;

import com.cosmos.videochat.dto.Greeting;
import com.cosmos.videochat.dto.HelloMessage;
import com.cosmos.videochat.dto.OfferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.HtmlUtils;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private  final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/app/videochat")
    public ResponseEntity<?> sendTospecificeUser(@RequestBody OfferDto dto){
        messagingTemplate.convertAndSendToUser(dto.getUsername(),"/topic/video/chat", dto);
        System.out.println(dto.getUsername());
        return null;
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }



}
