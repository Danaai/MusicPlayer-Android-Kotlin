package com.example.musicapp.ui.login_out.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Patterns
import com.example.musicapp.domain.repository.AuthRepository

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update {
            it.copy(username = value, errorMessage = null)
        }
    }

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(email = value, errorMessage = null)
        }
    }

    fun onPassChange(value: String) {
        _uiState.update {
            it.copy(password = value, errorMessage = null)
        }
    }

    fun onConfirmPassChange(value: String) {
        _uiState.update {
            it.copy(confirmPass = value, errorMessage = null)
        }
    }

    fun onTermsClick() {
        _uiState.update {
            it.copy(isTermsAccepted = !it.isTermsAccepted)
        }
    }

    fun onRegisterClick(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.isLoading) return

        val username = state.username.trim()
        val email = state.email.trim()
        val password = state.password.trim()

        when {
            username.isBlank() ||
                    email.isBlank() ||
                    password.isBlank() ||
                    state.confirmPass.isBlank() -> {
                updateError("All fields are required")
                return
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                updateError("Invalid email format")
                return
            }

            password.length < 6 -> {
                updateError("Password must be at least 6 characters")
                return
            }

            password != state.confirmPass -> {
                updateError("Passwords do not match")
                return
            }

            !state.isTermsAccepted -> {
                updateError("Please accept the Terms of Service")
                return
            }
        }

        _uiState.update {
            it.copy(isLoading = true, errorMessage = null)
        }

        viewModelScope.launch {
            val result = authRepository.register(
                username = username,
                email = email,
                password = password
            )

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            password = "",
                            confirmPass = "",
                            isLoading = false
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    updateError(error.message ?: "Register failed")
                }
            )
        }
    }

    private fun updateError(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = message
            )
        }
    }
}

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPass: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isTermsAccepted: Boolean = false,
)