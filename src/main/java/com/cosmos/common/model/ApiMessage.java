package com.cosmos.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ApiMessage {
    private String message;
    private HttpStatus status;
}
