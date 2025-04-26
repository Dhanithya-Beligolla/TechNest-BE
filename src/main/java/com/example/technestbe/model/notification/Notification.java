package com.example.technestbe.model.notification;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long recipientId;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private String content;

    private Long postId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Notification(Long recipientId, Long senderId, String content, Long postId, String type) {
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.content = content;
        this.postId = postId;
        this.type = type;
    }
}