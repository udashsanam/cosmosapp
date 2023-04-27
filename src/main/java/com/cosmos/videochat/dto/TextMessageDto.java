package com.cosmos.videochat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class TextMessageDto {
    private String sender;

    private String data;

    private String receiver;
}
