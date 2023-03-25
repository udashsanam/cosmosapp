package com.cosmos.videochat.scheduler;

import com.cosmos.login.repo.AppUserRepo;
import com.cosmos.videochat.util.WebSocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class UserScheduler {

    private SimpUserRegistry userRegistry;

    private AppUserRepo appUserRepo;

    public UserScheduler(SimpUserRegistry userRegistry,
                         AppUserRepo appUserRepo) {
        this.userRegistry = userRegistry;
        this.appUserRepo = appUserRepo;
    }

    @Scheduled(cron = "* */5 * * * *")
    private void connectedUser(){
        List<String> stringList = WebSocketUtil.getConnectedUsers(userRegistry);
        log.info("connected users ");
        stringList.stream().forEach( x-> {
        log.info(appUserRepo.findByUserId(Long.parseLong(x)).getFirstName());
        });

    }
}
