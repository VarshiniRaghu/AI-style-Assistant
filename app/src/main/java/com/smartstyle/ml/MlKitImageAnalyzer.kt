package com.smartstyle.ml

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MlKitImageAnalyzer @Inject constructor() {

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.60f)
            .build()
    )

    private val fashionKeywords = listOf(
        "Jeans", "Pants", "Trousers", "Shirt", "Tee", "T-shirt", "Top",
        "Dress", "Skirt", "Coat", "Jacket", "Blazer", "Sweater",
        "Bag", "Handbag", "Backpack", "Shoes", "Sneakers", "Boots",
        "Fashion", "Clothing", "Outfit", "Hat", "Cap", "Glasses",
        "Watch", "Scarf", "Outerwear", "Person", "Human body"
    )

    suspend fun getLabels(bitmap: Bitmap): List<String> {
        val input = InputImage.fromBitmap(bitmap, 0)
        return labeler.process(input).await()
            .filter { it.confidence >= 0.60f }
            .map { it.text }
            .filter { label -> fashionKeywords.any { label.contains(it, ignoreCase = true) } }
    }
}
