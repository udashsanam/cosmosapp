package com.cosmos.questionPool.service;

import com.cosmos.astromode.enitity.AstroModeEntity;
import com.cosmos.common.exception.CustomException;
import com.cosmos.login.dto.CurrentlyLoggedInUser;
import com.cosmos.login.entity.Role;
import com.cosmos.moderator.dto.ModeratorDto;
import com.cosmos.notification.model.Notification;
import com.cosmos.notification.model.NotificationDataPayload;
import com.cosmos.notification.model.NotificationResponse;
import com.cosmos.questionPool.dto.EnglishUnclearQuestionDto;
import com.cosmos.questionPool.entity.EnglishAnswerPool;
import com.cosmos.questionPool.entity.EnglishQuestionPool;
import com.cosmos.questionPool.entity.QuestionStatus;
import com.cosmos.user.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class NepaliQuestionPoolService {


    private String getRole() {
        CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection<GrantedAuthority> simpleGrantedAuthorities =  currentlyLoggedInUser.getAuthorities();
        return simpleGrantedAuthorities.iterator().next().getAuthority();
    }


    private Long getCurrentUserId() {
        CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentlyLoggedInUser.getCurrentlyLoggedInUserId();
    }
}
