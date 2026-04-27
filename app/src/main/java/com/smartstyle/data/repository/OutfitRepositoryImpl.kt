package com.smartstyle.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.smartstyle.data.local.OutfitAnalysisDao
import com.smartstyle.data.local.OutfitAnalysisEntity
import com.smartstyle.data.local.toDomain
import com.smartstyle.data.remote.GeminiService
import com.smartstyle.domain.model.OutfitAnalysis
import com.smartstyle.ml.MlKitImageAnalyzer
import com.smartstyle.util.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class OutfitRepositoryImpl @Inject constructor(
    private val geminiService: GeminiService,
    private val mlKitAnalyzer: MlKitImageAnalyzer,
    private val dao: OutfitAnalysisDao,
    @ApplicationContext private val context: Context
) : OutfitRepository {

    override suspend fun analyzeOutfit(bitmap: Bitmap, imageUri: String): Result<OutfitAnalysis> {
        return try {
            val labels = mlKitAnalyzer.getLabels(bitmap)
            val analysis = geminiService.analyzeOutfit(bitmap, labels)
            Result.Success(analysis.copy(imageUri = imageUri, mlKitLabels = labels))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveAnalysis(analysis: OutfitAnalysis, bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            val imageFile = File(context.filesDir, "analyses/${analysis.timestamp}.jpg")
            imageFile.parentFile?.mkdirs()
            imageFile.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it) }

            dao.insert(
                OutfitAnalysisEntity(
                    imageUri = imageFile.absolutePath,
                    assessment = analysis.assessment,
                    suggestions = analysis.suggestions.joinToString("\n"),
                    tags = analysis.tags.joinToString(","),
                    mlKitLabels = analysis.mlKitLabels.joinToString(","),
                    timestamp = analysis.timestamp
                )
            )
        }
    }

    override fun getHistory(): Flow<List<OutfitAnalysis>> =
        dao.getAllAnalyses().map { entities -> entities.map { it.toDomain() } }

    override suspend fun deleteAnalysis(id: Long) {
        withContext(Dispatchers.IO) {
            dao.deleteById(id)
        }
    }
}
