package com.example.musicapp.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.repository.MusicRepository
import com.example.musicapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminViewModel(
    private val musicRepository: MusicRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadAdmin()
        observeStats()
    }

    private fun loadAdmin() {
        viewModelScope.launch {
            userRepository.observeCurrentUser().collect { admin ->

                if(admin == null) {
                    _uiState.update {
                        it.copy(
                            isLoggedIn = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoggedIn = true
                        )
                    }
                }
            }
        }
    }

    private fun observeStats() {
        viewModelScope.launch {
            combine(
                musicRepository.getAllSongs(),
                userRepository.getAllUsers(),
                musicRepository.getAllArtists(),
                musicRepository.getAllAlbums()
            ) { songs, users, artists, albums ->
                AdminUiState(
                    totalSongs = songs.size,
                    totalUsers = users.size,
                    totalArtists = artists.size,
                    totalAlbums = albums.size
                )
            }.collect { stats ->
                _uiState.update {
                    it.copy(
                        totalSongs = stats.totalSongs,
                        totalUsers = stats.totalUsers,
                        totalArtists = stats.totalArtists,
                        totalAlbums = stats.totalAlbums
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    fun refreshAdmin() {
        loadAdmin()
    }

}

data class AdminUiState(
    val isLoggedIn: Boolean = false,

    val totalSongs: Int = 0,
    val totalUsers: Int = 0,
    val totalArtists: Int = 0,
    val totalAlbums: Int = 0
)