package com.cosmos.astrologer.dto;

import com.cosmos.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuestionerDetails {
    private Long questionId;
    private String question;
    private User user;
    private List<User> possibleDuplicateUsers;
    private List<PreviouslyAskedQuestions> previousQueries;

}
