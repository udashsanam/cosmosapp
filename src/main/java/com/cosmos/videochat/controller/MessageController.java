package com.cosmos.videochat.controller;

import com.cosmos.user.entity.User;
import com.cosmos.videochat.dto.AppUserDto;
import com.cosmos.videochat.dto.Greeting;
import com.cosmos.videochat.dto.HelloMessage;
import com.cosmos.videochat.dto.OfferDto;
import com.cosmos.videochat.service.VideoChatService;
import com.cosmos.videochat.util.WebSocketUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MessageController {


    private  final SimpMessagingTemplate messagingTemplate;

    private  final VideoChatService videoChatService;

    private final SimpUserRegistry userRegistry;





    public MessageController(SimpMessagingTemplate messagingTemplate,
                             VideoChatService videoChatService,
                             SimpUserRegistry simpUserRegistry) {
        this.messagingTemplate = messagingTemplate;
        this.videoChatService = videoChatService;
        this.userRegistry = simpUserRegistry;
    }

    @PostMapping("/api/videochat")
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

    @GetMapping("/api/getactiveusers")
    public ResponseEntity<?> getActiveUsers(){
        List<String> usernames = WebSocketUtil.getConnectedUsers(userRegistry);
        List<Long> userIds = usernames.stream().map(s -> Long.parseLong(s)).collect(Collectors.toList());
        List<AppUserDto> appUserDtos = videoChatService.getActiveWebUsers(userIds);

        return ResponseEntity.ok(appUserDtos);

    }

    @GetMapping("/api/test")
    public ResponseEntity<?> test(){

        return ResponseEntity.ok("test successful");
    }





}
