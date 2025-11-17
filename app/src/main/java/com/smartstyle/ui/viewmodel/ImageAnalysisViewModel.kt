package com.smartstyle.ui.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.core.graphics.decodeBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstyle.data.repository.AIRepository
import com.smartstyle.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageAnalysisViewModel @Inject constructor(
    private val repo: AIRepository,
    private val context: Context
) : ViewModel() {

    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap: StateFlow<Bitmap?> = _bitmap.asStateFlow()

    private val _labels = MutableStateFlow<Result<List<String>>>(Result.Success(emptyList()))
    val labels: StateFlow<Result<List<String>>> = _labels.asStateFlow()

    private val _embedding = MutableStateFlow<Result<FloatArray>>(Result.Success(FloatArray(0)))
    val embedding: StateFlow<Result<FloatArray>> = _embedding.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Simple similarity result: list of (id, score)
    private val _similarItems = MutableStateFlow<List<Pair<String, Float>>>(emptyList())
    val similarItems = _similarItems.asStateFlow()

    fun setBitmap(bitmap: Bitmap) {
        _bitmap.value = bitmap
    }

    fun clearResults() {
        _labels.value = Result.Success(emptyList())
        _embedding.value = Result.Success(FloatArray(0))
        _similarItems.value = emptyList()
        _error.value = null
        _loading.value = false
    }

    fun setError(msg: String) {
        _error.value = msg
    }

    fun loadBitmapFromUri(context: Context, uri: Uri)  = viewModelScope.launch {
        // Called from composable using context and URI
        val bmp = uriToBitmap(context.contentResolver, uri)
        bmp?.let { _bitmap.value = it }
    }

    private fun uriToBitmap(resolver: ContentResolver, uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val src = ImageDecoder.createSource(resolver, uri)
                ImageDecoder.decodeBitmap(src) { decoder, _, _ ->
                    decoder.isMutableRequired = false
                }
            } else {
                @Suppress("DEPRECATION")
                android.provider.MediaStore.Images.Media.getBitmap(resolver, uri)
            }
        } catch (e: Exception) {
            _error.value = "Failed to load image: ${e.message}"
            null
        }
    }

    fun analyzeCurrentImage() {
        val bmp = _bitmap.value ?: run {
            _error.value = "No image selected"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            // Get ML Kit labels
            when (val lblRes = repo.getImageLabels(bmp)) {
                is Result.Success -> _labels.value = Result.Success(lblRes.data)
                is Result.Error -> _labels.value = Result.Error(lblRes.exception)
            }

            // Get embedding
            when (val embRes = repo.getImageEmbedding(bmp)) {
                is Result.Success -> {
                    _embedding.value = Result.Success(embRes.data)
                    // Optionally calculate similarity using stored embeddings (local or retrieved)
                    // Here we use a placeholder function findSimilarItemsInCatalog()
                    _similarItems.value = findSimilarItemsInCatalog(embRes.data)
                }
                is Result.Error -> _embedding.value = Result.Error(embRes.exception)
            }

            _loading.value = false
        }
    }

    // Example placeholder: compare to a small in-app set of embeddings (you'll replace with real)
    private fun findSimilarItemsInCatalog(query: FloatArray): List<Pair<String, Float>> {
        // TODO: Load real item vectors from assets or backend. This is an example.
        val dummy = listOf(
            "coat_123" to 0.87f,
            "jeans_987" to 0.71f,
            "bag_555" to 0.64f
        )
        return dummy
    }
}
