package com.smartstyle.ui.screens.analyse

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstyle.data.repository.OutfitRepository
import com.smartstyle.domain.model.OutfitAnalysis
import com.smartstyle.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class AnalyseUiState {
    object Idle : AnalyseUiState()
    data class ImageReady(val bitmap: Bitmap, val uri: Uri) : AnalyseUiState()
    data class Analysing(val bitmap: Bitmap) : AnalyseUiState()
    data class Success(val analysis: OutfitAnalysis, val bitmap: Bitmap, val saved: Boolean = false) : AnalyseUiState()
    data class Error(val message: String) : AnalyseUiState()
}

@HiltViewModel
class AnalyseViewModel @Inject constructor(
    private val repository: OutfitRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnalyseUiState>(AnalyseUiState.Idle)
    val uiState: StateFlow<AnalyseUiState> = _uiState

    fun setImage(uri: Uri) {
        viewModelScope.launch {
            val bitmap = loadBitmap(uri)
            if (bitmap != null) {
                _uiState.value = AnalyseUiState.ImageReady(bitmap, uri)
            } else {
                _uiState.value = AnalyseUiState.Error("Could not load image")
            }
        }
    }

    fun analyzeOutfit() {
        val current = _uiState.value as? AnalyseUiState.ImageReady ?: return
        viewModelScope.launch {
            _uiState.value = AnalyseUiState.Analysing(current.bitmap)
            when (val result = repository.analyzeOutfit(current.bitmap, current.uri.toString())) {
                is Result.Success -> _uiState.value = AnalyseUiState.Success(result.data, current.bitmap)
                is Result.Error -> _uiState.value = AnalyseUiState.Error(
                    result.exception.message ?: "Analysis failed"
                )
            }
        }
    }

    fun saveAnalysis() {
        val current = _uiState.value as? AnalyseUiState.Success ?: return
        if (current.saved) return
        viewModelScope.launch {
            repository.saveAnalysis(current.analysis, current.bitmap)
            _uiState.value = current.copy(saved = true)
        }
    }

    fun reset() {
        _uiState.value = AnalyseUiState.Idle
    }

    internal fun setState(state: AnalyseUiState) {
        _uiState.value = state
    }

    private suspend fun loadBitmap(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            null
        }
    }
}
