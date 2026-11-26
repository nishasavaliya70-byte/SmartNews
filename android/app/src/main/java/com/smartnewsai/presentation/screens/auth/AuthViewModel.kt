package com.smartnewsai.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartnewsai.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoginMode: Boolean = true,
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _authSuccess = MutableSharedFlow<Unit>()
    val authSuccess: SharedFlow<Unit> = _authSuccess.asSharedFlow()

    fun toggleAuthMode() {
        _uiState.value = _uiState.value.copy(
            isLoginMode = !_uiState.value.isLoginMode,
            errorMessage = null
        )
    }

    fun onFullNameChange(name: String) {
        _uiState.value = _uiState.value.copy(fullName = name, errorMessage = null)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun onConfirmPasswordChange(confirm: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirm, errorMessage = null)
    }

    fun submit() {
        val state = _uiState.value
        val email = state.email.trim()
        val password = state.password

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = state.copy(errorMessage = "Please enter a valid email address.")
            return
        }

        if (password.length < 6) {
            _uiState.value = state.copy(errorMessage = "Password must be at least 6 characters.")
            return
        }

        if (!state.isLoginMode) {
            if (state.fullName.trim().length < 2) {
                _uiState.value = state.copy(errorMessage = "Please enter your full name.")
                return
            }
            if (password != state.confirmPassword) {
                _uiState.value = state.copy(errorMessage = "Passwords do not match.")
                return
            }
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = if (state.isLoginMode) {
                authRepository.login(email = email, password = password)
            } else {
                authRepository.register(
                    email = email,
                    fullName = state.fullName.trim(),
                    password = password
                )
            }

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _authSuccess.emit(Unit)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Authentication failed."
                )
            }
        }
    }
}
