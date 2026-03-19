package com.example.musicapp.ui.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.AuthRepository
import com.example.musicapp.domain.repository.MusicRepository
import com.example.musicapp.player.ExoMusicController
import com.example.musicapp.player.MusicController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewModel(
    application: Application,
    private val repository: MusicRepository,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    private val controller: MusicController =
        ExoMusicController.getInstance(application)

    private val storage = PlayerStateStorage(application)

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var queue: MutableList<Song> = mutableListOf()
    private var currentIndex = -1
    private var currentUserId: Long = -1

    init {
        observePlayer()

        loadCurrentUser()

        restoreLastSong()

        controller.setOnSongCompletionListener {
            playNext()
        }
    }

    private fun observePlayer() {

        viewModelScope.launch {
            controller.isPlaying.collect { playing ->
                _uiState.update { it.copy(isPlaying = playing) }
            }
        }

        viewModelScope.launch {
            controller.durationMs.collect { duration ->
                _uiState.update { it.copy(durationMs = duration) }
            }
        }

        viewModelScope.launch {
            controller.currentPositionMs.collect { pos ->
                val duration = _uiState.value.durationMs
                val progress =
                    if (duration > 0) pos.toFloat() / duration else 0f

                _uiState.update {
                    it.copy(
                        currentPositionMs = pos,
                        progress = progress
                    )
                }
            }
        }

        viewModelScope.launch {
            controller.currentSong.collect { song ->

                if (song != null && currentUserId != -1L) {
                    viewModelScope.launch {
                        repository.addRecentlyPlayed(currentUserId, song.id)
                    }
                }

                val index = queue.indexOfFirst { it.id == song?.id }

                var artistName = ""
                var isFavorite = false

                if (song != null) {

                    val artist = repository.getArtistById(song.artistId)
                    artistName = artist?.name ?: ""

                    isFavorite = repository.isFavorite(currentUserId, song.id)
                }

                _uiState.update {
                    it.copy(
                        currentSong = song,
                        currentIndex = index,
                        currentArtistName = artistName,
                        isFavorite = isFavorite
                    )
                }

                currentIndex = index
            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val user = authRepository.observeCurrentUser().first()
            currentUserId = user?.id ?: -1
        }
    }

    private fun restoreLastSong() {

        val state = storage.load() ?: return

        if(state.position < 3000) return

        viewModelScope.launch {

            val song = repository.getSongById(state.songId) ?: return@launch

            queue = mutableListOf(song)
            currentIndex = 0

            (controller as ExoMusicController)
                .restoreState(song, state.position, state.isPlaying)
        }
    }

    // =============================
    // PLAY QUEUE
    // =============================

    fun playFromQueue(songs: List<Song>, startSong: Song) {
        queue = songs.toMutableList()
        currentIndex = songs.indexOfFirst { it.id == startSong.id }
        if (currentIndex == -1) return

        _uiState.update {
            it.copy(
                queue = queue,
                currentSong = startSong,
                currentIndex = currentIndex
            )
        }

        controller.play(startSong)
    }

    fun playFromQueueAt(index: Int) {
        if (index !in queue.indices) return
        currentIndex = index
        val song = queue[index]

        _uiState.update {
            it.copy(currentSong = song, currentIndex = index)
        }

        controller.play(song)
    }

    fun playNext() {
        if (queue.isEmpty()) return

        val nextIndex = when {
            uiState.value.isShuffle ->
                queue.indices.random()

            currentIndex + 1 < queue.size ->
                currentIndex + 1

            uiState.value.isRepeat ->
                0

            else -> return
        }

        playFromQueueAt(nextIndex)
    }

    fun playPrevious() {
        val prevIndex = when {
            currentIndex - 1 in queue.indices -> currentIndex - 1
            uiState.value.isRepeat -> queue.lastIndex
            else -> return
        }

        playFromQueueAt(prevIndex)
    }

    // =============================
    // CONTROLS
    // =============================

    fun togglePlayPause() {
        controller.togglePlayPause()
    }

    fun seekTo(progress: Float) {
        val duration = uiState.value.durationMs
        controller.seekTo((duration * progress).toLong())
    }

    fun toggleShuffle() {
        _uiState.update { it.copy(isShuffle = !it.isShuffle) }
    }

    fun toggleRepeat() {
        _uiState.update { it.copy(isRepeat = !it.isRepeat) }
    }

    fun reorderQueue(from: Int, to: Int) {
        if (from !in queue.indices || to !in queue.indices) return

        val item = queue.removeAt(from)
        queue.add(to, item)

        _uiState.update { it.copy(queue = queue.toList()) }
    }

    fun toggleFavorite() {
        val song = uiState.value.currentSong ?: return

        viewModelScope.launch {

            val newState = !uiState.value.isFavorite

            if (newState) {
                repository.addFavorite(currentUserId, song.id)
            } else {
                repository.removeFavorite(currentUserId, song.id)
            }

            _uiState.update {
                it.copy(isFavorite = newState)
            }
        }
    }
}

data class PlayerUiState(
    val currentSong: Song? = null,
    val currentArtistName: String = "",
    val isFavorite: Boolean = false,
    val queue: List<Song> = emptyList(),
    val currentIndex: Int = -1,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val durationMs: Long = 0L,
    val currentPositionMs: Long = 0L,
    val isShuffle: Boolean = false,
    val isRepeat: Boolean = false
)

