package com.example.soundmark.ui.views.musicDetail

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
        // 1. 반드시 viewModelScope.launch를 먼저 열어줘야 합니다! (코루틴 시작)
        viewModelScope.launch {
            _uiState.value = MusicDetailUiState.Loading

            try {
                // 2. 이제 이 안에서는 suspend 함수를 자유롭게 호출할 수 있습니다.
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
                // 위치를 가져오는 도중 발생할 수 있는 에러 처리
                _uiState.value = MusicDetailUiState.Error("위치 서비스 오류: ${e.message}")
            }
        }
    }

    /**
     * 리액션을 추가하거나 취소하는 비즈니스 로직입니다.
     */
    fun toggleReaction(type: ReactionType) {
        val currentState = _uiState.value
        // 1. 현재 상태가 Success일 때만 로직을 수행합니다.
        if (currentState is MusicDetailUiState.Success) {
            val mark = currentState.soundMark
            var updatedReactions = mark.reactions.toMutableList()

            val myOldReactionIndex = updatedReactions.indexOfFirst { it.isReactedByMe }
            val isClickingSameReaction = myOldReactionIndex != -1 && updatedReactions[myOldReactionIndex].type.id == type.id

            // 2. 기존에 눌렀던 리액션이 있다면 먼저 처리합니다.
            if (myOldReactionIndex != -1) {
                val oldReaction = updatedReactions[myOldReactionIndex]
                // 기존 리액션의 카운트를 줄이고 상태를 해제합니다.
                updatedReactions[myOldReactionIndex] = oldReaction.copy(
                    count = (oldReaction.count - 1).coerceAtLeast(0),
                    isReactedByMe = false
                )
            }

            // 3. 같은 리액션을 다시 누른 게 아니라면 (즉, 새로운 리액션을 선택한 경우)
            if (!isClickingSameReaction) {
                val targetIndex = updatedReactions.indexOfFirst { it.type.id == type.id }
                if (targetIndex != -1) {
                    // 이미 목록에 있는 리액션이면 카운트만 올립니다.
                    val existing = updatedReactions[targetIndex]
                    updatedReactions[targetIndex] = existing.copy(
                        count = existing.count + 1,
                        isReactedByMe = true
                    )
                } else {
                    // 목록에 없는 새로운 리액션이면 새로 추가합니다.
                    updatedReactions.add(Reaction(type, 1, true))
                }
            }

            // 4. [수정 사항 1 반영] 숫자가 0이 된 리액션들은 리스트에서 삭제합니다.
            updatedReactions = updatedReactions.filter { it.count > 0 }.toMutableList()

            // 5. 최종 상태 업데이트
            _uiState.value = currentState.copy(
                soundMark = mark.copy(reactions = updatedReactions)
            )

            // 6. 서버 통신 (Repository API 설계에 따라 변경 필요)
            viewModelScope.launch {
                // 실제 환경에서는 기존 리액션 취소 API와 새 리액션 추가 API를 각각 호출하거나,
                // 서버에서 한 번에 처리하도록 설계합니다.
                repository.postReaction(mark.id, type.id)
            }
        }
    }
}