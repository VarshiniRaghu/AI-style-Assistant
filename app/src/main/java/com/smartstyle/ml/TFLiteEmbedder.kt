package com.smartstyle.ml

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TFLiteEmbedder(private val context: Context) {
    private var interpreter: Interpreter? = null

    fun loadModel(assetFileName: String = "model.tflite") {
        val fileDescriptor = context.assets.openFd(assetFileName)
        val inputStream = fileDescriptor.createInputStream()
        val bytes = inputStream.readBytes()
        val buffer = ByteBuffer.allocateDirect(bytes.size).apply {
            order(ByteOrder.nativeOrder())
            put(bytes)
            rewind()
        }
        interpreter = Interpreter(buffer)
    }

    fun getEmbedding(bitmap: Bitmap): FloatArray {
        // preprocess into model input shape then run interpreter.run(...)
        // returns float[] embedding
        return FloatArray(512) // placeholder
    }

    fun close() { interpreter?.close() }
}
