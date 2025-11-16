package com.smartstyle.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIService{
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") auth:String,
        @Body req: ChatRequest
    ): ChatResponse
}

data class ChatRequest(val model:String, val messages:List<Message>)
data class Message(val role:String, val content:String)
data class ChatResponse(val choices:List<Choice>)
data class Choice(val message: Message)
