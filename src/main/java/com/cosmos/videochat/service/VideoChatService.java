package com.cosmos.videochat.service;

import com.cosmos.login.entity.AppUser;
import com.cosmos.videochat.dto.AppUserDto;

import java.util.List;

public interface VideoChatService {

    List<AppUserDto> getActiveWebUsers(List<Long> userIds);
}
