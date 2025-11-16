package com.smartstyle.ml

import android.graphics.Bitmap
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.smartstyle.util.Result

object MLKitVisionProcessor {
    fun processImage(bitmap: Bitmap): Result<List<String>> {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            val task = Tasks.await(labeler.process(image)) // prefer coroutine wrapper in real code
            val labels = task.map { it.text }
            Result.Success(labels)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
