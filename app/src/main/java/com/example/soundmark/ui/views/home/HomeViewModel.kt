package com.example.soundmark.ui.views.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundmark.data.model.GeoLocation
import com.example.soundmark.data.model.MapPin
import com.example.soundmark.data.model.SoundMark
import com.example.soundmark.data.repository.profile.MarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val markRepository: MarkRepository
) : ViewModel() {

    // 지도에 표시할 마크 리스트 상태
    private val _mapPins = MutableStateFlow<List<MapPin>>(emptyList())
    val mapPins: StateFlow<List<MapPin>> = _mapPins.asStateFlow()

    init {
        loadNearbyPins(GeoLocation.Default)
    }

    fun loadNearbyPins(location: GeoLocation) {
        viewModelScope.launch {
            // ✅ suspend 함수 호출 및 Result 처리
            val result = markRepository.getNearbyMarks(location)
            result.onSuccess { pins ->
                _mapPins.value = pins
            }.onFailure {
                // TODO: 에러 처리 로직 (Toast 등)
            }
        }
    }
}