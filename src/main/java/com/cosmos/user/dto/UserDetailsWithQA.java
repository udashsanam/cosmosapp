package com.cosmos.user.dto;

import com.cosmos.questionPool.projection.QuestionAnswerHistory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class UserDetailsWithQA {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String city;
    private String state;
    private String country;
    private String profileImageUrl;
    private String dateOfBirth;
    private String birthTime;
    private String phoneNumber;
    private Boolean accurateTime;
    private String deviceToken;
    private List<QuestionAnswerHistory> questionAnswerHistoryList;
}


