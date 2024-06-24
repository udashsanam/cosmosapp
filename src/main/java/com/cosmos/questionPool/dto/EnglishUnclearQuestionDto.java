package com.cosmos.questionPool.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EnglishUnclearQuestionDto {
    private Long engQuestionId;
    private String description;
    private Long assignedModId;
    private Long userId;
    // for unclear by mode and astro mode pre unclear by astrologer
    private Long nepQuestionId;

    private Long nepAnswerId;
}
