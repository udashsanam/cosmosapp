package com.cosmos.moderator.service;

import com.cosmos.moderator.dto.ModeratorDto;
import com.cosmos.moderator.dto.QuestionAnswerPoolForModerator;

import java.util.List;

public interface ModeratorService {
    ModeratorDto addModerator(ModeratorDto moderatorDto);

    ModeratorDto updateModerator(Long moderatorId, ModeratorDto moderatorDto);

    List<ModeratorDto> findAllModerator();

    ModeratorDto findModeratorById(Long id);

    QuestionAnswerPoolForModerator findModeratorUnfinishedTask();

    void deleteModeratorById(Long id);

    QuestionAnswerPoolForModerator findAstroModeratorUnfinishedTask();
}
