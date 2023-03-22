package com.cosmos.questionPool.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NepaliQuestionDto {
    private Long engQsnId;
    private String convertedQsn;
    private Long userId;
}
