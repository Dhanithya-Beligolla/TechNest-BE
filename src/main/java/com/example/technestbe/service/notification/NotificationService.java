package com.example.technestbe.service.notification;

import com.example.technestbe.model.notification.Notification;
import com.example.technestbe.repo.notification.NotificationRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final WebSocketService webSocketService;

    @Transactional
    public Notification createNotification(Long recipientId, Long senderId, String content, Long postId, String type) {
        Notification notification = new Notification(recipientId, senderId, content, postId, type);
        notification = notificationRepository.save(notification);
        webSocketService.sendNotification(recipientId.toString(), notification);
        return notification;
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }
}
