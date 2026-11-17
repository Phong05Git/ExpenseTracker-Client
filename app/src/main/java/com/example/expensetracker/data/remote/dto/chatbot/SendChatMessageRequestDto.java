package com.example.expensetracker.data.remote.dto.chatbot;

/** Request DTO for sending a user message to an existing conversation. */
public class SendChatMessageRequestDto {

    private final String content;

    public SendChatMessageRequestDto(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
