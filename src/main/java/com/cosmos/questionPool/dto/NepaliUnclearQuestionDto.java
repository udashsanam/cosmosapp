package com.cosmos.questionPool.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NepaliUnclearQuestionDto {
    private Long nepaliQuestionId;
    private String description;
    private Long userId;
}
