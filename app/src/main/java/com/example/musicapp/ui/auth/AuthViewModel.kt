package com.example.musicapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.User
import com.example.musicapp.domain.repository.AuthRepository
import com.example.musicapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> =
        userRepository.observeCurrentUser()
            .map { user ->
                if (user == null)
                    AuthState.LoggedOut
                else
                    AuthState.LoggedIn(user)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                AuthState.Loading
            )

    suspend fun logout() {
        userRepository.logout()
    }
}

sealed class AuthState {
    object Loading : AuthState()
    data class LoggedIn(val user: User) : AuthState()
    object LoggedOut : AuthState()
}