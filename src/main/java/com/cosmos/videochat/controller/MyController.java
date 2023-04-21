package com.cosmos.videochat.controller;

import com.cosmos.login.entity.AppUser;
import com.cosmos.login.repo.AppUserRepo;
import com.cosmos.videochat.config.SocketHandler;
import com.cosmos.videochat.dto.AppUserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@RestController("/api/video-chat")
public class MyController {

    private final AppUserRepo appUserRepo;

    public MyController(AppUserRepo appUserRepo) {
        this.appUserRepo = appUserRepo;
    }


    @GetMapping("/getactiveusers")
    public ResponseEntity<?> getActiveUsers(){
        Map<WebSocketSession, String> sessions = SocketHandler.sessions;
        Set<WebSocketSession> onlineUser = sessions.keySet();
        List<AppUserDto> appUserDtos = new ArrayList<>();

        for (WebSocketSession webSocketSession :
                onlineUser) {
            Long userid = Long.parseLong(sessions.get(onlineUser));
            String userType = appUserRepo.findUserType(userid);
                if(userType.equals("moderator") || userType.equals("astrologer")){
                    AppUser appUser = appUserRepo.getById(userid);
                    appUserDtos.add(new AppUserDto(appUser.getUserId(), appUser.getFirstName() + appUser.getLastName()));
                }
        }

        return new ResponseEntity<>(appUserDtos, HttpStatus.OK);

    }




}
