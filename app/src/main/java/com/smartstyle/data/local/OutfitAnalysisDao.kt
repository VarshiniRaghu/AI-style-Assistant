package com.smartstyle.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitAnalysisDao {
    @Query("SELECT * FROM outfit_analyses ORDER BY timestamp DESC")
    fun getAllAnalyses(): Flow<List<OutfitAnalysisEntity>>

    @Insert
    suspend fun insert(entity: OutfitAnalysisEntity): Long

    @Query("DELETE FROM outfit_analyses WHERE id = :id")
    suspend fun deleteById(id: Long)
}
