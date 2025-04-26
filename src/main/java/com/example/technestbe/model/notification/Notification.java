package com.example.technestbe.model.notification;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class representing a notification in the system.
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Recipient ID cannot be null")
    @Column(nullable = false)
    private Long recipientId;

    @NotNull(message = "Sender ID cannot be null")
    @Column(nullable = false)
    private Long senderId;

    @NotBlank(message = "Content cannot be blank")
    @Column(nullable = false)
    private String content;

    private Long postId;

    @NotBlank(message = "Type cannot be blank")
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private boolean isRead = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;

    public Notification(Long recipientId, Long senderId, String content, Long postId, String type) {
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.content = content;
        this.postId = postId;
        this.type = type;
    }
}