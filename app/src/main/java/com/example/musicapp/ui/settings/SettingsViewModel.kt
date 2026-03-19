package com.example.musicapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {

            userRepository.observeCurrentUser().collect { user ->

                if(user == null) {
                    _uiState.update {
                        it.copy(isLoggedIn = false)
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoggedIn = true,
                            userName = user.username,
                            email = user.email,
                            avatarUrl = user.avtUrl,
                        )
                    }
                }
            }
        }
    }

    fun onDarkModeChange(enabled: Boolean) {
        _uiState.update {
            it.copy(isDarkMode = enabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    fun refreshUser() {
        loadUser()
    }

}

data class SettingsUiState(
    val isLoggedIn: Boolean = false,

    val userName: String = "",
    val email: String? = "",
    val avatarUrl: String? = null,

    val isDarkMode: Boolean = false,
)