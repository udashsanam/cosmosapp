package com.cosmos.videochat.util;

import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;

import java.util.List;
import java.util.stream.Collectors;

public class WebSocketUtil {
    private static SimpUserRegistry simpUserRegistry;


    public static   List<String> getAllActiveUsers(){

        return simpUserRegistry.getUsers().stream().map(SimpUser::getName).collect(Collectors.toList());

    }

}
