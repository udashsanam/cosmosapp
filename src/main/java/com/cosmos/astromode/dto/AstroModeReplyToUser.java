package com.cosmos.astromode.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AstroModeReplyToUser {

    private Long engQuesId;
    private Long userId;
    private String translatedAns;

    private Long astroModeratorId;
}
