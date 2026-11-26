package com.smartnewsai.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartnewsai.data.preferences.PreferenceManager
import com.smartnewsai.presentation.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface SplashUiState {
    object Loading : SplashUiState
    data class Navigate(val route: String) : SplashUiState
}

class SplashViewModel(
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkInitialDestination()
    }

    private fun checkInitialDestination() {
        viewModelScope.launch {
            // Keep splash visible with polished animation for 2.2 seconds
            delay(2200)

            val isOnboardingCompleted = preferenceManager.isOnboardingCompleted.first()
            val authToken = preferenceManager.authToken.first()

            val destination = when {
                !isOnboardingCompleted -> Screen.Onboarding.route
                authToken.isNullOrEmpty() -> Screen.Login.route
                else -> Screen.Main.route
            }

            _uiState.value = SplashUiState.Navigate(destination)
        }
    }
}
