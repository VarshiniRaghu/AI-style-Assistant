package com.smartstyle.data.repository

import android.graphics.Bitmap
import com.smartstyle.data.remote.OpenAIService
import com.smartstyle.data.remote.ChatRequest
import com.smartstyle.data.remote.Message
import com.smartstyle.ml.EmbeddingExtractor
import com.smartstyle.ml.MLKitVisionProcessor
import com.smartstyle.ml.MlKitImageAnalyzer
import com.smartstyle.util.Result
import javax.inject.Inject

private const val YOUR_KEY = ""
class AIRepositoryImpl @Inject constructor(
    private val api: OpenAIService,
    private val context: android.content.Context
): AIRepository{
    override suspend fun getChatReply(prompt: String): Result<String>{
        return try{
            val req = ChatRequest("gpt-4o-mini", listOf(Message("user", prompt)))
            val res = api.createChatCompletion("Bearer $YOUR_KEY", req)
            Result.Success(res.choices.first().message.content)
        }catch(e:Exception){ Result.Error(e) }
    }
    override suspend fun analyzeImageForLabels(bitmap: Bitmap): Result<List<String>> {
        // Delegates to MLKitVisionProcessor (see next)
        return MLKitVisionProcessor.processImage(bitmap)
    }

    override suspend fun getSimilarItems(embedding: FloatArray): Result<List<Item>> {
        // stub: call your backend or local search for nearest neighbors
        return Result.Success(emptyList())
    }

    override suspend fun getImageLabels(bitmap: Bitmap): Result<List<String>> {
        return try {
            val labels = MlKitImageAnalyzer.getLabels(bitmap) // suspend
            Result.Success(labels)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getImageEmbedding(bitmap: Bitmap): Result<FloatArray> {
        return try {
            val extractor = EmbeddingExtractor.getInstance(context)
            val vec = extractor.extract(bitmap)
            Result.Success(vec)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}
