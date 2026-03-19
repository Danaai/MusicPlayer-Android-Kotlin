package com.example.musicapp.ui.login_out.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update {
            it.copy(
                username = value,
                errorMessage = null
            )
        }
    }

    fun onPassChange(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                errorMessage = null
            )
        }
    }

    fun onLoginClick(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.isLoading) return

        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Username or Password can't be empty")
            }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val result = authRepository.login(
                username = state.username.trim(),
                email = state.email.trim(),
                password = state.password.trim()
            )

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            password = "",
                            isLoading = false
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Login failed"
                        )
                    }
                }
            )
        }
    }
}

data class LoginUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)