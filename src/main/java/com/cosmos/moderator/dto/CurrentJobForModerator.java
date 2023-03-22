package com.cosmos.moderator.dto;

import com.cosmos.astrologer.entity.NepaliAnswerPool;
import com.cosmos.questionPool.entity.EnglishQuestionPool;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CurrentJobForModerator {
    private String currentJobType;
    private String processUrl;
    private EnglishQuestionPool englishQuestion;
    private NepaliAnswerPool nepaliAnswer;
}
