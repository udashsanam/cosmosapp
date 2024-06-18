package com.cosmos.astromode.service;

import com.cosmos.astromode.dto.AstroModeDto;
import com.cosmos.astromode.dto.AstroModeReplyToUser;
import com.cosmos.moderator.dto.AstrologerReplyToEng;
import com.cosmos.moderator.dto.QuestionAnswerPoolForModerator;
import com.cosmos.questionPool.entity.EnglishAnswerPool;
import com.cosmos.user.dto.UserDto;
import com.cosmos.user.entity.User;

public interface IAstroModeService {

    AstroModeDto saveAstroMode(AstroModeDto astroModeDto);

    AstroModeDto updateModerator(Long astroModeId, AstroModeDto astroModeDto);

    AstroModeDto findModeratorById(Long id);

    void deleteModeratorById(Long id);

    QuestionAnswerPoolForModerator fetchAstroModeCurrentJob();

    EnglishAnswerPool saveFinalAnswer(AstroModeReplyToUser astroModeReplyToUser, Long userId);

    QuestionAnswerPoolForModerator findAstroModeratorUnfinishedTask();

    EnglishAnswerPool storeTranslatedReply(AstrologerReplyToEng astroRep, Long id);
}
