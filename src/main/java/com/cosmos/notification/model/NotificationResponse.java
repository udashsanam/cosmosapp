package com.cosmos.notification.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationResponse {
    private boolean success;
    private String failureMsg;
    private String messageId;
}
