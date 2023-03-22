package com.cosmos.moderator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModeratorOrAstrologerResponse {
    private String name;
    private String userType;
    private int total_responded_qs;
    private List<QsAns> qs_answers;
}
