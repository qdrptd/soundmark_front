package com.example.soundmark.ui.views.musicDetail

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

    /**
     * 리액션을 추가하거나 취소하는 비즈니스 로직입니다.
     */
    fun toggleReaction(type: ReactionType) {
        val currentState = _uiState.value
        if (currentState is MusicDetailUiState.Success) {
            val mark = currentState.soundMark
            var updatedReactions = mark.reactions.toMutableList()

            // 1. 내가 현재 누른 리액션이 있는지 확인
            val myOldReactionIndex = updatedReactions.indexOfFirst { it.isReactedByMe }
            val isClickingSameReaction = myOldReactionIndex != -1 && updatedReactions[myOldReactionIndex].type.emoji == type.emoji

            viewModelScope.launch {
                if (isClickingSameReaction) {
                    // [취소 로직] 이미 누른 걸 또 누름 -> 삭제
                    val oldReaction = updatedReactions[myOldReactionIndex]
                    updatedReactions[myOldReactionIndex] = oldReaction.copy(
                        count = (oldReaction.count - 1).coerceAtLeast(0),
                        isReactedByMe = false
                    )
                    
                    // 서버에 삭제 요청
                    repository.deleteReaction(mark.id)
                } else {
                    // [등록/교체 로직]
                    // 기존에 다른 걸 눌렀었다면 먼저 로컬 카운트 차감
                    if (myOldReactionIndex != -1) {
                        val oldReaction = updatedReactions[myOldReactionIndex]
                        updatedReactions[myOldReactionIndex] = oldReaction.copy(
                            count = (oldReaction.count - 1).coerceAtLeast(0),
                            isReactedByMe = false
                        )
                    }

                    // 새 리액션 추가 또는 카운트 증가
                    val targetIndex = updatedReactions.indexOfFirst { it.type.emoji == type.emoji }
                    if (targetIndex != -1) {
                        val existing = updatedReactions[targetIndex]
                        updatedReactions[targetIndex] = existing.copy(
                            count = existing.count + 1,
                            isReactedByMe = true
                        )
                    } else {
                        updatedReactions.add(Reaction(type, 1, true))
                    }

                    // 서버에 등록(PUT) 요청
                    repository.putReaction(mark.id, type.emoji)
                }

                // 숫자가 0이 된 리액션 제거 및 상태 업데이트
                val finalReactions = updatedReactions.filter { it.count > 0 }
                _uiState.value = currentState.copy(
                    soundMark = mark.copy(reactions = finalReactions)
                )
            }
        }
    }
}
