package com.example.expensetracker.domain.model;

/** Domain model representing one message in a chat session. */
public class ChatMessage {

    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";

    private final int id;
    private final int chatSessionId;
    private final String role;
    private final String content;
    private final String createdAt;

    public ChatMessage(
            int id,
            int chatSessionId,
            String role,
            String content,
            String createdAt) {
        this.id = id;
        this.chatSessionId = chatSessionId;
        this.role = role == null ? "" : role;
        this.content = content == null ? "" : content;
        this.createdAt = createdAt;
    }

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

    public boolean isUserMessage() {
        return ROLE_USER.equalsIgnoreCase(role);
    }

    public boolean isAssistantMessage() {
        return ROLE_ASSISTANT.equalsIgnoreCase(role);
    }
}
