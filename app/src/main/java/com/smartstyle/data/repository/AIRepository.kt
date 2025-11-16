package com.smartstyle.data.repository

import com.smartstyle.util.Result
import android.graphics.Bitmap

data class Item(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String = ""
)

interface AIRepository{
    suspend fun getChatReply(prompt: String): Result<String>
    suspend fun analyzeImageForLabels(bitmap: Bitmap): Result<List<String>>
    suspend fun getSimilarItems(embedding: FloatArray): Result<List<Item>>
}
