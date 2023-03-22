package com.cosmos.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
public class UserHistoryDto {
    @Deprecated
    private List<String> welcomeMessages;

    private List<Map<String, Object>> messages;

    private UserDetailsWithQA userDetailsWithQA;
}
