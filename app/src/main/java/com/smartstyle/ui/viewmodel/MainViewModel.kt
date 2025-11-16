package com.smartstyle.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstyle.data.repository.AIRepository
import com.smartstyle.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repo: AIRepository): ViewModel() {

    private val _chat = MutableStateFlow<String?>(null)
    val chat: StateFlow<String?> = _chat

    fun askAssistant(prompt: String){
        viewModelScope.launch {
            when(val res = repo.getChatReply(prompt)) {
                is Result.Success -> _chat.value = res.data
                is Result.Error -> _chat.value = "Error: ${res.exception.message}"
            }
        }
    }
}
