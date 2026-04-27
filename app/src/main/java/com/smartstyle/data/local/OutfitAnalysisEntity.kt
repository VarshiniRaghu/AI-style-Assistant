package com.smartstyle.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smartstyle.domain.model.OutfitAnalysis

@Entity(tableName = "outfit_analyses")
data class OutfitAnalysisEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imageUri: String,
    val assessment: String,
    val suggestions: String,
    val tags: String,
    val mlKitLabels: String,
    val timestamp: Long = System.currentTimeMillis()
)

fun OutfitAnalysisEntity.toDomain() = OutfitAnalysis(
    id = id,
    imageUri = imageUri,
    assessment = assessment,
    suggestions = suggestions.split("\n").filter { it.isNotBlank() },
    tags = tags.split(",").filter { it.isNotBlank() },
    mlKitLabels = mlKitLabels.split(",").filter { it.isNotBlank() },
    timestamp = timestamp
)
