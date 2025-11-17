package com.smartstyle.ml

import android.graphics.Bitmap
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

object ImagePreprocessor {

    fun preprocess(bitmap: Bitmap): TensorImage {
        // Convert bitmap → TensorImage
        var tensorImage = TensorImage.fromBitmap(bitmap)

        // Preprocessing pipeline for MobileNetV3
        val processor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f)) // scale inputs to [0,1]
            .build()

        tensorImage = processor.process(tensorImage)
        return tensorImage
    }
}
