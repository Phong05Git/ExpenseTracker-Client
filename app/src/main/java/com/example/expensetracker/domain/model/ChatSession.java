package com.example.expensetracker.domain.model;

/**
 * Domain model representing one conversation owned by the authenticated user.
 * User ownership is enforced by the Backend and is intentionally not supplied
 * by the Android client.
 */
public class ChatSession {

    private final int id;
    private final String title;
    private final String lastMessagePreview;
    private final String createdAt;
    private final String updatedAt;

    public ChatSession(
            int id,
            String title,
            String lastMessagePreview,
            String createdAt,
            String updatedAt) {
        this.id = id;
        this.title = title == null ? "Cuộc trò chuyện mới" : title;
        this.lastMessagePreview = lastMessagePreview == null ? "" : lastMessagePreview;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLastMessagePreview() {
        return lastMessagePreview;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
