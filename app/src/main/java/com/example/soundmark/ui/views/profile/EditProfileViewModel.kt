package com.example.soundmark.ui.views.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundmark.data.repository.profile.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // 사용자의 실시간 입력값 관리 (ProfileViewModel의 데이터와 분리)
    val displayName = MutableStateFlow("")
    val statusMessage = MutableStateFlow("")
    val selectedImageId = MutableStateFlow(1)

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * 편집 화면 진입 시 기존 데이터를 세팅합니다.
     * 네비게이션 인자로 넘겨받거나, 초기 1회 getMe()를 통해 채울 수 있습니다.
     */
    fun setupData(name: String, message: String, imageId: Int) {
        displayName.value = name
        statusMessage.value = message
        selectedImageId.value = imageId
    }

    fun updateProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = userRepository.updateProfile(
                name = displayName.value,
                imageId = selectedImageId.value,
                message = statusMessage.value
            )

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Update failed") }
            }
        }
    }
}