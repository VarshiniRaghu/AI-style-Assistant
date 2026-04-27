package com.smartstyle.data.repository

import android.graphics.Bitmap
import com.smartstyle.domain.model.OutfitAnalysis
import com.smartstyle.util.Result
import kotlinx.coroutines.flow.Flow

interface OutfitRepository {
    suspend fun analyzeOutfit(bitmap: Bitmap, imageUri: String): Result<OutfitAnalysis>
    suspend fun saveAnalysis(analysis: OutfitAnalysis, bitmap: Bitmap)
    fun getHistory(): Flow<List<OutfitAnalysis>>
    suspend fun deleteAnalysis(id: Long)
}
