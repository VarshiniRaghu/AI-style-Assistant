package com.smartstyle.domain.model

data class OutfitAnalysis(
    val id: Long = 0,
    val imageUri: String = "",
    val assessment: String,
    val suggestions: List<String>,
    val tags: List<String>,
    val mlKitLabels: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)
