package com.cosmos.user.dto;

import com.cosmos.astrologer.projection.AstrologerReplyProjection;
import com.cosmos.questionPool.projection.EnglishReplyProjection;
import com.cosmos.questionPool.projection.NepaliQuestionProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserQuestionAnswerHistory {
    private String engQuestion;
    private String status;
    private String createdAt;
    private NepaliQuestionProjection translatedEngQuestion;
    private AstrologerReplyProjection nepaliAnswer;
    private EnglishReplyProjection englishAnswer;
}
