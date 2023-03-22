package com.cosmos.moderator.dto;

import com.cosmos.user.dto.UserDto;
import com.cosmos.user.dto.UserQuestionAnswerHistory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class QuestionAnswerPoolForModerator {
   private CurrentJobForModerator currentJob;
   private UserDto userDetails;
   private List<UserQuestionAnswerHistory> questionAnswerHistoryList;
}
