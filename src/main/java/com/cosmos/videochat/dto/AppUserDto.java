package com.cosmos.videochat.dto;

import lombok.Data;

@Data
public class AppUserDto {

    private Long id;
    private String fullName;

    public AppUserDto(Long id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }
}
