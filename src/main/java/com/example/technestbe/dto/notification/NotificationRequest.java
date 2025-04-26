package com.example.technestbe.dto.notification;

import lombok.Data;

@Data
public class NotificationRequest {
    private Long recipientId;
    private Long senderId;
    private Long postId;
}
