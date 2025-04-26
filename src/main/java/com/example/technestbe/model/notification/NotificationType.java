package com.example.technestbe.model.notification;

public enum NotificationType {
    LIKE("LIKE"),
    COMMENT("COMMENT"),
    POST("POST");

    private final String type;

    NotificationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}