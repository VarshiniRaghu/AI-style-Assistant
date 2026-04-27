package com.smartstyle.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstyle.data.repository.OutfitRepository
import com.smartstyle.domain.model.OutfitAnalysis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: OutfitRepository
) : ViewModel() {

    val history: StateFlow<List<OutfitAnalysis>> = repository.getHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun deleteAnalysis(id: Long) {
        viewModelScope.launch { repository.deleteAnalysis(id) }
    }
}
