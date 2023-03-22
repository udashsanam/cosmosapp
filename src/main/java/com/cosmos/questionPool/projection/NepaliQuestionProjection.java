package com.cosmos.questionPool.projection;

public interface NepaliQuestionProjection {
    Long getNepQuestionId();
    String getTranslatedQuestion();
    String getTranslatedBy();
    String getTranslatedOn();
}
