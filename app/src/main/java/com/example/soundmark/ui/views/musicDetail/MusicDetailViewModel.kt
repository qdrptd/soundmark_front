package com.example.soundmark.ui.views.musicDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundmark.data.model.SoundMark
import com.example.soundmark.data.repository.songDetail.SoundMarkDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class MusicDetailUiState {
    object Loading : MusicDetailUiState()
    data class Success(val soundMark: SoundMark) : MusicDetailUiState()
    data class Error(val message: String) : MusicDetailUiState()
}

@HiltViewModel
class MusicDetailViewModel @Inject constructor(
    private val repository: SoundMarkDetailRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MusicDetailUiState>(MusicDetailUiState.Loading)
    val uiState: StateFlow<MusicDetailUiState> = _uiState

    fun loadSoundMark(id: String) {
        viewModelScope.launch {
            _uiState.value = MusicDetailUiState.Loading
            repository.getSoundMarkById(id)
                .onSuccess { _uiState.value = MusicDetailUiState.Success(it) }
                .onFailure { _uiState.value = MusicDetailUiState.Error("데이터를 가져오지 못했습니다.") }
        }
    }
}