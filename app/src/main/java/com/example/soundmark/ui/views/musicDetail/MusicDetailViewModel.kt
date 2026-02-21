package com.example.soundmark.ui.views.musicDetail

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundmark.data.model.Reaction
import com.example.soundmark.data.model.ReactionType
import com.example.soundmark.data.model.SoundMark
import com.example.soundmark.data.repository.songDetail.SoundMarkDetailRepository
import com.example.soundmark.util.LocationService
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
    private val repository: SoundMarkDetailRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _uiState = MutableStateFlow<MusicDetailUiState>(MusicDetailUiState.Loading)
    val uiState: StateFlow<MusicDetailUiState> = _uiState

    private var mediaPlayer: MediaPlayer? = null

    fun loadSoundMark(id: String) {
        viewModelScope.launch {
            _uiState.value = MusicDetailUiState.Loading

            try {
                val currentLocation = locationService.getCurrentLocation()

                if (currentLocation != null) {
                    repository.getSoundMarkById(
                        id = id,
                        lat = currentLocation.latitude,
                        lng = currentLocation.longitude
                    ).onSuccess { soundMark ->
                        _uiState.value = MusicDetailUiState.Success(soundMark)
                        // 노래 로드 성공 시 미리보기 재생 시도
                        soundMark.track.previewUrl?.let { url ->
                            playPreview(url)
                        }
                    }.onFailure { exception ->
                        _uiState.value = MusicDetailUiState.Error(exception.message ?: "데이터 로드 실패")
                    }
                } else {
                    _uiState.value = MusicDetailUiState.Error("위치 정보를 가져올 수 없습니다.")
                }
            } catch (e: Exception) {
                _uiState.value = MusicDetailUiState.Error("위치 서비스 오류: ${e.message}")
            }
        }
    }

    private fun playPreview(url: String) {
        try {
            stopPreview() // 기존 재생 중인 것이 있다면 중지
            
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { 
                    it.start()
                    Log.d("MusicDetailViewModel", "Preview playback started")
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("MusicDetailViewModel", "MediaPlayer error: $what, $extra")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e("MusicDetailViewModel", "Failed to play preview: ${e.message}")
        }
    }

    fun stopPreview() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPreview() // 뷰모델이 파괴될 때 재생 중지 및 자원 해제
    }

    /**
     * 리액션을 추가하거나 취소하는 비즈니스 로직입니다.
     */
    fun toggleReaction(type: ReactionType) {
        val currentState = _uiState.value
        if (currentState is MusicDetailUiState.Success) {
            val mark = currentState.soundMark
            var updatedReactions = mark.reactions.toMutableList()

            val myOldReactionIndex = updatedReactions.indexOfFirst { it.isReactedByMe }
            val isClickingSameReaction = myOldReactionIndex != -1 && updatedReactions[myOldReactionIndex].type.id == type.id

            if (myOldReactionIndex != -1) {
                val oldReaction = updatedReactions[myOldReactionIndex]
                updatedReactions[myOldReactionIndex] = oldReaction.copy(
                    count = (oldReaction.count - 1).coerceAtLeast(0),
                    isReactedByMe = false
                )
            }

            if (!isClickingSameReaction) {
                val targetIndex = updatedReactions.indexOfFirst { it.type.id == type.id }
                if (targetIndex != -1) {
                    val existing = updatedReactions[targetIndex]
                    updatedReactions[targetIndex] = existing.copy(
                        count = existing.count + 1,
                        isReactedByMe = true
                    )
                } else {
                    updatedReactions.add(Reaction(type, 1, true))
                }
            }

            updatedReactions = updatedReactions.filter { it.count > 0 }.toMutableList()

            _uiState.value = currentState.copy(
                soundMark = mark.copy(reactions = updatedReactions)
            )

            viewModelScope.launch {
                repository.postReaction(mark.id, type.id)
            }
        }
    }
}
