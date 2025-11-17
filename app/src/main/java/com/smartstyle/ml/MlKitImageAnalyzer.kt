package com.smartstyle.ml

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.tasks.await

object MlKitImageAnalyzer {

    // On-device image labeler
    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.60f) // Only return labels >= 60% confidence
            .build()
    )

    /**
     * Extracts labels from an image using ML Kit On-Device Image Labeling.
     * Automatically handles conversion of Bitmap -> InputImage.
     */
    suspend fun getLabels(bitmap: Bitmap): List<String> {
        val input = InputImage.fromBitmap(bitmap, 0)
        val result = labeler.process(input).await()

        val labels = result.map { it.text }

        // Optional: Filter out non-fashion labels
        return filterFashionLabels(labels)
    }

    /**
     * Optional: Since ML Kit returns general-purpose labels,
     * we can make results more "fashion-specific".
     */
    private fun filterFashionLabels(labels: List<String>): List<String> {
        val fashionKeywords = listOf(
            "Jeans", "Pants", "Trousers", "Shirt", "Tee", "T-shirt", "Top",
            "Dress", "Skirt", "Coat", "Jacket", "Blazer", "Sweater",
            "Bag", "Handbag", "Backpack", "Shoes", "Sneakers", "Boots",
            "Fashion", "Clothing", "Outfit", "Hat", "Cap", "Glasses",
            "Watch", "Scarf", "Outerwear"
        )

        return labels.filter { label ->
            fashionKeywords.any { keyword ->
                label.contains(keyword, ignoreCase = true)
            }
        }
    }
}
