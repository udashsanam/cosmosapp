package com.cosmos.videochat.service;

import com.cosmos.login.entity.AppUser;
import com.cosmos.login.repo.AppUserRepo;
import com.cosmos.videochat.dto.AppUserDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VideoChatServiceImpl implements VideoChatService{

    private final AppUserRepo appUserRepo;

    public VideoChatServiceImpl(AppUserRepo appUserRepo) {
        this.appUserRepo = appUserRepo;
    }

    @Override
    public List<AppUserDto> getActiveWebUsers(List<Long> userIds) {
        List<AppUserDto> appUserDtos = userIds.stream().map(userId -> {
            String userType = appUserRepo.findUserType(userId);
            if((userType.equals("moderator") || userType.equals("astrologer"))){
                return appUserRepo.findAppUserDtoById(userId);
            }
            return null;
        }).collect(Collectors.toList());
            appUserDtos.removeAll(Collections.singleton(null));
        return appUserDtos;
    }

}
