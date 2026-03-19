package com.example.musicapp.ui.editProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.User
import com.example.musicapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private var originalUser: User? = null

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUser = true) }

            userRepository.observeCurrentUser().collect { user ->

                if (user != null) {

                    originalUser = user

                    _uiState.update {
                        it.copy(
                            userName = user.username,
                            email = user.email,
                            bio = user.bio ?: "",
                            avatarUrl = user.avtUrl,
                            isLoadingUser = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoadingUser = false) }
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update {
            it.copy(
                userName = value,
                errorMessage = null
            )
        }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onBioChange(value: String) {
        _uiState.update { it.copy(bio = value) }
    }

    fun onAvatarChange(url: String) {
        _uiState.update { it.copy(avatarUrl = url) }
    }

    fun onSave() {
        val state = _uiState.value
        if (state.isSaving) return

        val trimmedName = state.userName.trim()

        if (trimmedName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Name cannot be empty") }
            return
        }

        if (!hasChanges()) {
            _uiState.update { it.copy(saveSuccess = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val updatedUser = originalUser?.copy(
                username = trimmedName,
                bio = state.bio,
                avtUrl = state.avatarUrl
            ) ?: return@launch

            try {
                userRepository.saveUser(updatedUser)

                originalUser = updatedUser

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "Failed to save profile"
                    )
                }
            }
        }
    }

    private fun hasChanges(): Boolean {
        val state = _uiState.value
        val user = originalUser ?: return false

        return state.userName.trim() != user.username ||
                state.bio != (user.bio ?: "") ||
                state.avatarUrl != user.avtUrl
    }

    fun onSaveHandled() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}

data class EditProfileUiState(
    val userName: String = "",
    val email: String = "",
    val bio: String? = null,
    val avatarUrl: String? = null,

    val isLoadingUser: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)