package com.smartnewsai.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartnewsai.data.preferences.PreferenceManager
import com.smartnewsai.domain.model.UserProfile
import com.smartnewsai.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userProfile: UserProfile = UserProfile(
        id = "",
        fullName = "SmartNews User",
        email = "user@smartnews.ai",
        selectedInterests = listOf("Technology", "Science", "Business")
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = authRepository.getCurrentUserProfile()
            result.onSuccess { profile ->
                _uiState.value = _uiState.value.copy(
                    userProfile = profile,
                    isLoading = false
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferenceManager.setDarkMode(enabled)
            val current = _uiState.value.userProfile
            _uiState.value = _uiState.value.copy(
                userProfile = current.copy(isDarkMode = enabled)
            )
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        val current = _uiState.value.userProfile
        _uiState.value = _uiState.value.copy(
            userProfile = current.copy(notificationsEnabled = enabled)
        )
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLoggedOut()
        }
    }
}
