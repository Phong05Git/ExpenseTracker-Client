package com.example.expensetracker.data.remote.dto.chatbot;

/** Response DTO returned by the Backend when listing chat sessions. */
public class ChatSessionDto {

    private int id;
    private String title;
    private String lastMessagePreview;
    private String createdAt;
    private String updatedAt;

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
