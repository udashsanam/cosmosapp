package com.cosmos.videochat.controller;

import com.cosmos.login.entity.AppUser;
import com.cosmos.login.repo.AppUserRepo;
import com.cosmos.videochat.config.SocketHandler;
import com.cosmos.videochat.dto.AppUserDto;
import com.cosmos.videochat.dto.TextMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/video-chat")
public class VideoChatController {

    private final AppUserRepo appUserRepo;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public VideoChatController(AppUserRepo appUserRepo, SimpMessagingTemplate simpMessagingTemplate) {
        this.appUserRepo = appUserRepo;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    @GetMapping("/getactiveusers")
    public ResponseEntity<?> getActiveUsers(){
        Map<WebSocketSession, String> sessions = SocketHandler.sessions;
        Set<WebSocketSession> onlineUser = sessions.keySet();
        List<AppUserDto> appUserDtos = new ArrayList<>();

        for (WebSocketSession webSocketSession :
                onlineUser) {
            Long userid = Long.parseLong(sessions.get(webSocketSession));
            String userType = appUserRepo.findUserType(userid);
                if(userType.equals("moderator") || userType.equals("astrologer")){
                    AppUser appUser = appUserRepo.getById(userid);
                    appUserDtos.add(new AppUserDto(appUser.getUserId(), appUser.getFirstName() + appUser.getLastName()));
                }
        }

        return new ResponseEntity<>(appUserDtos, HttpStatus.OK);

    }

    @PostMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@RequestBody TextMessageDto textMessageDto, Authentication authentication) throws IOException {

        Map<WebSocketSession, String> sessions = SocketHandler.sessions;
        Set<WebSocketSession> onlineUser = sessions.keySet();
        List<AppUserDto> appUserDtos = new ArrayList<>();
        int x = 0;

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        for (Map.Entry<WebSocketSession, String> entry:
                sessions.entrySet()) {
            if(entry.getValue().equals(textMessageDto.getReceiver())){

                TextMessageDto textMessageDto1 = new TextMessageDto();
                textMessageDto1.setSender(appUserRepo.findByEmail(userDetails.getUsername()).getUserId().toString());
                textMessageDto1.setData(textMessageDto.getData());

                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = ow.writeValueAsString(textMessageDto1);

                entry.getKey().sendMessage(new TextMessage(json));
                x++;

            }
        }
        if(x == 0){
            return ResponseEntity.ok("user offline");
        }

        return ResponseEntity.ok("successfully send");


    }




}
