package com.example.soundmark.ui.views.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundmark.data.model.Profile
import com.example.soundmark.data.repository.profile.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val isMe: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String = savedStateHandle["userId"] ?: "me"

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // 1. Get current user's ID to compare
            val meResult = userRepository.getMe()
            val myId = meResult.getOrNull()?.id

            // 2. Fetch the target profile
            val profileResult = if (userId == "me") {
                userRepository.getMyProfile()
            } else {
                userRepository.getProfileByUserId(userId)
            }

            profileResult.onSuccess { profile ->
                val isMe = userId == "me" || profile.user.id == myId
                _uiState.update { it.copy(isLoading = false, profile = profile, isMe = isMe) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load profile") }
            }
        }
    }
}
