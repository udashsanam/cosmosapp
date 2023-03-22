package com.cosmos.questionPool.projection;

import com.cosmos.questionPool.entity.QuestionStatus;

import java.util.Date;

public interface QuestionAnswerHistory {
    String getEngQuestion();
    String getAnswer();
    String getRepliedBy();
    String getProfileImgUrl();
    QuestionStatus getStatus();
    Date getCreatedAt();
}
