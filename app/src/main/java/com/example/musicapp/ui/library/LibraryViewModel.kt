package com.example.musicapp.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LibraryTab {
    PLAYLISTS,
    ARTISTS,
    ALBUMS,
    SONGS
}

class LibraryViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

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
                            avatarUrl = user.avtUrl,
                        )
                    }
                }
            }
        }
    }

    fun selectTab(tab: LibraryTab) {
        _uiState.value = _uiState.value.copy(
            selectedTab = tab
        )
    }

}

data class LibraryUiState(
    val selectedTab: LibraryTab = LibraryTab.PLAYLISTS,

    val isLoggedIn: Boolean = false,
    val avatarUrl: String? = null,
)