package com.smartstyle.data.remote

import com.squareup.moshi.JsonClass
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

@JsonClass(generateAdapter = true)
data class ChatRequest(val model:String, val messages:List<Message>)

@JsonClass(generateAdapter = true)
data class Message(val role:String, val content:String)

@JsonClass(generateAdapter = true)
data class ChatResponse(val choices:List<Choice>)

@JsonClass(generateAdapter = true)
data class Choice(val message: Message)
