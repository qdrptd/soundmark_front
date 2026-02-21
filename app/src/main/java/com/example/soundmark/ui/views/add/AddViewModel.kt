package com.example.soundmark.ui.views.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundmark.data.model.GeoLocation
import com.example.soundmark.data.model.Track
import com.example.soundmark.data.repository.map.MapRepository
import com.example.soundmark.util.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.soundmark.data.repository.spotify.SpotifyRepository

data class AddUiState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val currentLocation: GeoLocation? = null,
    val nearbyPlaces: List<GeoLocation> = emptyList(),
    val searchedTracks: List<Track> = emptyList(),
    val selectedPlace: GeoLocation? = null,
    val selectedTrack: Track? = null,
    val error: String? = null,
    val permissionDenied: Boolean = false
)

@HiltViewModel
class AddViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val spotifyRepository: SpotifyRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    fun onPlaceSelected(place: GeoLocation) {
        _uiState.value = _uiState.value.copy(selectedPlace = place)
    }

    fun onTrackSelected(track: Track) {
        _uiState.value = _uiState.value.copy(selectedTrack = track)
    }

    fun searchTracks(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchedTracks = emptyList())
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            val result = spotifyRepository.searchTracks(query)
            result.onSuccess { tracks ->
                _uiState.value = _uiState.value.copy(searchedTracks = tracks, isSearching = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isSearching = false, error = "곡 검색 실패: ${e.message}")
            }
        }
    }

    fun fetchNearbyPlacesWithGPS() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val location = locationService.getCurrentLocation()
            if (location != null) {
                val result = mapRepository.searchNearbyPlaces(location.latitude, location.longitude)
                result.onSuccess { places ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        nearbyPlaces = places,
                        currentLocation = places.firstOrNull() ?: location
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "장소를 가져오지 못했습니다: ${exception.message}"
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "현재 위치를 가져올 수 없습니다. GPS와 권한을 확인해주세요."
                )
            }
        }
    }

    fun onPermissionDenied() {
        _uiState.value = _uiState.value.copy(permissionDenied = true)
    }

}
