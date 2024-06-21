package com.cosmos.questionPool.projection;

public interface PreviouslyAskedQuestionsProjection {

     Long  getQuestionId();
     String getQuestion();
     String getAnswer();
     Long getUserId();
     String getRole();
}
