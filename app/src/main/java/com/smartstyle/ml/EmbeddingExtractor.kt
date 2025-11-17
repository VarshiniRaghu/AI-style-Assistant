package com.smartstyle.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class EmbeddingExtractor private constructor(context: Context) {

    private val interpreter: Interpreter

    init {
        val modelBytes = context.assets.open("mobilenet_v3_embed.tflite").readBytes()
        val byteBuffer = java.nio.ByteBuffer.wrap(modelBytes)
        interpreter = Interpreter(byteBuffer)
    }

    fun extract(bitmap: android.graphics.Bitmap): FloatArray {
        // Preprocess image
        val tensorImage = ImagePreprocessor.preprocess(bitmap)

        // Output buffer (1, 1024)
        val output = TensorBuffer.createFixedSize(
            intArrayOf(1, 1024),
            org.tensorflow.lite.DataType.FLOAT32
        )

        // Run inference
        interpreter.run(tensorImage.buffer, output.buffer.rewind())

        return output.floatArray
    }

    companion object {
        @Volatile private var INSTANCE: EmbeddingExtractor? = null

        fun getInstance(context: Context): EmbeddingExtractor {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: EmbeddingExtractor(context).also { INSTANCE = it }
            }
        }
    }
}
