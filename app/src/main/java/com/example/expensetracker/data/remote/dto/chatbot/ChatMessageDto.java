package com.example.expensetracker.data.remote.dto.chatbot;

/** Response DTO returned by the Backend when loading chat messages. */
public class ChatMessageDto {

    private int id;
    private int chatSessionId;
    private String role;
    private String content;
    private String createdAt;

    public int getId() {
        return id;
    }

    public int getChatSessionId() {
        return chatSessionId;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
