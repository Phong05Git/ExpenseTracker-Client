package com.example.expensetracker.data.remote.api;

import com.example.expensetracker.data.remote.dto.chatbot.ChatMessageDto;
import com.example.expensetracker.data.remote.dto.chatbot.ChatSessionDto;
import com.example.expensetracker.data.remote.dto.chatbot.CreateChatSessionRequestDto;
import com.example.expensetracker.data.remote.dto.chatbot.SendChatMessageRequestDto;
import com.example.expensetracker.data.remote.dto.common.ApiResponseDto;
import com.example.expensetracker.data.remote.dto.common.PagedResultDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatApi {

    @GET("api/chat/sessions")
    Call<ApiResponseDto<PagedResultDto<ChatSessionDto>>> getSessions(
            @Query("page") int page,
            @Query("pageSize") int pageSize);

    @POST("api/chat/sessions")
    Call<ApiResponseDto<ChatSessionDto>> createSession(
            @Body CreateChatSessionRequestDto request);

    @GET("api/chat/sessions/{sessionId}/messages")
    Call<ApiResponseDto<PagedResultDto<ChatMessageDto>>> getMessages(
            @Path("sessionId") int sessionId,
            @Query("page") int page,
            @Query("pageSize") int pageSize);

    @POST("api/chat/sessions/{sessionId}/messages")
    Call<ApiResponseDto<ChatMessageDto>> sendMessage(
            @Path("sessionId") int sessionId,
            @Body SendChatMessageRequestDto request);
}
