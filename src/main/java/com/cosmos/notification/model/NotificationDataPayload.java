package com.cosmos.notification.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationDataPayload {

    private String engQuestionId;
    private String status;
    private String message;
    private String repliedBy;
    private String profileImgUrl;
//    "click_action": "FLUTTER_NOTIFICATION_CLICK",

}
