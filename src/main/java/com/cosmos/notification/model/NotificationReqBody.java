package com.cosmos.notification.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class NotificationReqBody {
    private String to;
    private String collapse_key;
    private String priority;
    private Notification notification;
    private NotificationDataPayload data;
}
