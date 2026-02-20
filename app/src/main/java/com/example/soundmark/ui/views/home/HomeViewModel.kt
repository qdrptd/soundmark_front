package com.example.soundmark.ui.views.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundmark.data.model.SoundMark
import com.example.soundmark.data.repository.MusicRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<List<SoundMark>>(emptyList())
    val uiState: StateFlow<List<SoundMark>> = _uiState

    fun fetchNearbyMusic(currentLocation: LatLng) {
        viewModelScope.launch {
            repository.getNearbyMusicMarks(currentLocation).collect { marks ->
                _uiState.value = marks
            }
        }
    }
}