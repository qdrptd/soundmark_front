package com.example.soundmark.ui.views.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundmark.data.dto.CreateRecommendationRequestDto
import com.example.soundmark.data.dto.PlaceDto
import com.example.soundmark.data.model.GeoLocation
import com.example.soundmark.data.model.SimpleTrack
import com.example.soundmark.data.model.Track
import com.example.soundmark.data.repository.map.MapRepository
import com.example.soundmark.data.repository.soundmark.SoundMarkRepository
import com.example.soundmark.util.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.soundmark.data.repository.spotify.SpotifyRepository

data class AddUiState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val isPosting: Boolean = false,
    val isPostSuccess: Boolean = false,
    val currentLocation: GeoLocation? = null,
    val nearbyPlaces: List<GeoLocation> = emptyList(),
    val searchedTracks: List<Track> = emptyList(),
    val popularTracks: List<SimpleTrack> = emptyList(), // SimpleTrack으로 변경
    val selectedPlace: GeoLocation? = null,
    val selectedTrack: Track? = null,
    val error: String? = null,
    val permissionDenied: Boolean = false,
    val searchQuery: String = "",
    val message: String = ""
)

@OptIn(FlowPreview::class)
@HiltViewModel
class AddViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val spotifyRepository: SpotifyRepository,
    private val soundMarkRepository: SoundMarkRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        fetchPopularTracks()
        viewModelScope.launch {
            _searchQuery
                .debounce(500L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isNotBlank()) {
                        performSearch(query)
                    } else {
                        // 검색어 비었을 때 인기 곡 보여주는 기능 삭제
                        _uiState.update { it.copy(searchedTracks = emptyList(), isSearching = false) }
                    }
                }
        }
    }

    private fun fetchPopularTracks() {
        viewModelScope.launch {
            spotifyRepository.getPopularTracks()
                .onSuccess { tracks ->
                    _uiState.update { it.copy(popularTracks = tracks) }
                }
        }
    }

    fun onPlaceSelected(place: GeoLocation) {
        _uiState.value = _uiState.value.copy(selectedPlace = place)
    }

    fun onTrackSelected(track: Track) {
        _uiState.value = _uiState.value.copy(selectedTrack = track)
    }

    // SimpleTrack을 선택했을 때의 처리
    fun onSimpleTrackSelected(simpleTrack: SimpleTrack) {
        val track = Track(
            id = simpleTrack.id,
            title = simpleTrack.title,
            artist = simpleTrack.artist,
            albumCoverUrl = "", // SimpleTrack에는 정보가 없으므로 빈값 처리
            spotifyUrl = "",
            previewUrl = null
        )
        _uiState.value = _uiState.value.copy(selectedTrack = track)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onMessageChanged(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    private suspend fun performSearch(query: String) {
        _uiState.update { it.copy(isSearching = true) }
        val result = spotifyRepository.searchTracks(query)
        result.onSuccess { tracks ->
            _uiState.update { it.copy(searchedTracks = tracks, isSearching = false) }
        }.onFailure { e ->
            _uiState.update { it.copy(isSearching = false, error = "곡 검색 실패: ${e.message}") }
        }
    }

    fun postSoundMark() {
        val currentState = _uiState.value
        val track = currentState.selectedTrack
        val place = currentState.selectedPlace

        if (track == null || place == null) {
            _uiState.update { it.copy(error = "노래와 장소를 모두 선택해주세요.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isPosting = true, error = null) }
            
            val request = CreateRecommendationRequestDto(
                lat = place.latitude,
                lng = place.longitude,
                place = PlaceDto(
                    source = "google",
                    googlePlaceId = place.placeId ?: "",
                    placeName = place.placeName ?: "Unknown Place",
                    address = place.address ?: "Unknown Address"
                ),
                spotifyTrackId = track.id,
                message = currentState.message
            )

            soundMarkRepository.postRecommendation(request)
                .onSuccess {
                    _uiState.update { it.copy(isPosting = false, isPostSuccess = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isPosting = false, error = "저장 실패: ${e.message}") }
                }
        }
    }

    fun fetchNearbyPlacesWithGPS() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val location = locationService.getCurrentLocation()
            if (location != null) {
                val result = mapRepository.searchNearbyPlaces(location.latitude, location.longitude, radius = 200)
                result.onSuccess { places ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        nearbyPlaces = places,
                        currentLocation = location
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
