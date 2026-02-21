package com.example.soundmark.ui.views.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundmark.data.model.PlaceRecommendationCard
import com.example.soundmark.data.repository.recommendation.RecommendationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RecommendationUiState {
    object Loading : RecommendationUiState

    data class Success(
        val cards: List<PlaceRecommendationCard>
    ) : RecommendationUiState

    data class Error(
        val message: String? = "알 수 없는 에러가 발생했습니다."
    ) : RecommendationUiState
}

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val repository: RecommendationRepository
) : ViewModel() {

    // 내부에서만 수정 가능한 가변 상태
    private val _uiState = MutableStateFlow<RecommendationUiState>(RecommendationUiState.Loading)

    // UI에서 관찰하는 읽기 전용 상태
    val uiState: StateFlow<RecommendationUiState> = _uiState.asStateFlow()

    init {
        // ViewModel 생성 시 자동으로 데이터 로드
        fetchPlaceRecommendations()
    }

    /**
     * 추천 카드 목록을 가져옵니다.
     */
    fun fetchPlaceRecommendations() {
        viewModelScope.launch {
            _uiState.value = RecommendationUiState.Loading

            repository.getPlaceRecommendations()
                .onSuccess { cards ->
                    _uiState.value = RecommendationUiState.Success(cards)
                }
                .onFailure { exception ->
                    _uiState.value = RecommendationUiState.Error(exception.message)
                }
        }
    }
}