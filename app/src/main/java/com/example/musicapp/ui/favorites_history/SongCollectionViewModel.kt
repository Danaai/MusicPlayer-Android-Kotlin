package com.example.musicapp.ui.favorites_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.MusicRepository
import com.example.musicapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SongCollectionViewModel(
    private val userRepository: UserRepository,
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SongCollectionUiState())
    val uiState: StateFlow<SongCollectionUiState> = _uiState.asStateFlow()

    fun loadSongs(type: SongCollectionType) {
        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val user = userRepository.observeCurrentUser().first()

                if (user == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            songs = emptyList(),
                            originalSongs = emptyList()
                        )
                    }
                    return@launch
                }

                val songs = when (type) {
                    SongCollectionType.FAVORITES ->
                        musicRepository.getFavoriteSongs(user.id).first()

                    SongCollectionType.HISTORY ->
                        musicRepository.getRecentlyPlayed(user.id).first()
                }

                val artists = musicRepository.getAllArtists().first()

                updateState(type, songs, artists)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load songs"
                    )
                }
            }
        }
    }

    private fun updateState(
        type: SongCollectionType,
        songs: List<Song>,
        artists: List<Artist>
    ) {
        _uiState.update {
            it.copy(
                title = if (type == SongCollectionType.FAVORITES)
                    "Favorites"
                else
                    "History",
                songs = songs,
                originalSongs = songs,
                artists = artists,
                isLoading = false,
                isShuffled = false
            )
        }
    }

    fun playAll(): List<Song> {
        return _uiState.value.songs
    }

    fun shuffle() {
        _uiState.update { state ->
            if (state.isShuffled) {
                state.copy(
                    songs = state.originalSongs,
                    isShuffled = false
                )
            } else {
                state.copy(
                    songs = state.originalSongs.shuffled(),
                    isShuffled = true
                )
            }
        }
    }
}
data class SongCollectionUiState(
    val title: String = "",
    val songs: List<Song> = emptyList(),
    val originalSongs: List<Song> = emptyList(),
    val artists: List<Artist> = emptyList(),

    val isLoading: Boolean = false,
    val isShuffled: Boolean = false,
    val errorMessage: String? = null
)