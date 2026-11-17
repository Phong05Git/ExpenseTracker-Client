package com.example.expensetracker.data.remote.dto.chatbot;

/** Request DTO for creating a new conversation. */
public class CreateChatSessionRequestDto {

    private final String title;

    public CreateChatSessionRequestDto(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
