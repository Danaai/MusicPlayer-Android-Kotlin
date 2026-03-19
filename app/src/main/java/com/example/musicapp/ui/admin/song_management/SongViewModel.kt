package com.example.musicapp.ui.admin.song_management

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.MusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class SongManagementViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _deleteTarget = MutableStateFlow<Song?>(null)
    private val _currentPage = MutableStateFlow(1)

    val uiState: StateFlow<SongManagementUiState> =
        combine(
            repository.getAllSongs(),
            repository.getAllArtists(),
            _searchQuery,
            _deleteTarget,
            _currentPage
        ) { songs, artists, query, deleteTarget, page ->

            val artistMap = artists.associateBy { it.id }

            val mappedSongs = songs.map { song ->
                song to (artistMap[song.artistId]?.name ?: "Unknown")
            }

            val filtered = if (query.isBlank()) mappedSongs
            else mappedSongs.filter { it.first.title.contains(query, true) }

            val pageSize = 8
            val totalPages = (filtered.size + pageSize - 1) / pageSize

            val safePage = page.coerceIn(1, totalPages.coerceAtLeast(1))

            val pagedSongs = filtered
                .drop((safePage - 1)*pageSize)
                .take(pageSize)

            SongManagementUiState(
                songs = pagedSongs,
                searchQuery = query,
                showDeleteDialog = deleteTarget != null,
                songToDelete = deleteTarget,
                currentPage = safePage,
                totalPages = totalPages
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SongManagementUiState()
        )

    fun onSearchChange(value: String) {
        _searchQuery.value = value
    }

    fun onDeleteClick(song: Song) {
        _deleteTarget.value = song
    }

    fun dismissDeleteDialog() {
        _deleteTarget.value = null
    }

    fun confirmDelete() {
        viewModelScope.launch {
            _deleteTarget.value?.let {
                repository.deleteSong(it.id)
            }
            _deleteTarget.value = null
        }
    }

    fun nextPage() {
        _currentPage.update { it + 1 }
    }

    fun prevPage() {
        _currentPage.update { (it - 1).coerceAtLeast(1) }
    }

    fun goToPage(page: Int) {
        _currentPage.value = page
    }
}

data class SongManagementUiState(
    val songs: List<Pair<Song, String>> = emptyList(),
    val searchQuery: String = "",
    val showDeleteDialog: Boolean = false,
    val songToDelete: Song? = null,

    val currentPage: Int = 1,
    val totalPages: Int = 1
)

class AddSongViewModel(
    private val repository: MusicRepository,
    private val songId: Long? = null,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddSongUiState())
    val uiState: StateFlow<AddSongUiState> = _uiState.asStateFlow()

    private var allArtists: List<Artist> = emptyList()
    private var albumId: Long? = null

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    init {
        songId?.let { loadSong(it) }
        observeArtists()
    }

    private fun loadSong(id: Long) {
        viewModelScope.launch {
            repository.getSongById(id)?. let { song ->

                val song = repository.getSongById(id) ?: return@launch

                albumId = song.albumId

                val artist = repository.getArtistById(song.artistId)

                _uiState.update {
                    it.copy(
                        title = song.title,
                        artistQuery = "${artist?.id} - ${artist?.name}",
                        selectedArtistId = song.artistId,
                        audioPath = song.audioUrl,
                        imagePath = song.imageUrl,
                        duration = song.durationMs,
                        genre = song.genre ?: "",
                        releaseDate = song.releaseDate ?: "",
                    )
                }
            }
        }
    }

    private fun observeArtists() {
        viewModelScope.launch {
            repository.getAllArtists().collect {
                allArtists = it
            }
        }
    }

    fun onArtistQueryChange(query: String) {
        val filtered = if (query.isBlank()) {
            emptyList()
        } else {
            allArtists.filter {
                it.name.contains(query, true) ||
                        it.id.toString().contains(query)
            }
        }

        _uiState.update {
            it.copy(
                artistQuery = query,
                artistSuggestions = filtered,
                selectedArtistId = null
            )
        }
    }

    fun onArtistSelected(artist: Artist) {
        _uiState.update {
            it.copy(
                artistQuery = "${artist.id} - ${artist.name}",
                selectedArtistId = artist.id,
                artistSuggestions = emptyList()
            )
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun onImageSelected(uri: Uri) {
        val path = copyToInternalStorage(context, uri, "images")
        _uiState.update { it.copy(imagePath = path) }
    }

    fun onAudioSelected(uri: Uri) {
        val audioPath = copyToInternalStorage(context, uri, "audio")

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(audioPath)

        val durationStr =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

        val duration = durationStr?.toLongOrNull() ?: 0L
        retriever.release()

        _uiState.update {
            it.copy(
                audioPath = audioPath,
                duration = duration
            )
        }
    }

    fun onGenreChange(value: String) {
        _uiState.update { it.copy(genre = value) }
    }

    fun onReleaseDateChange(value: String) {
        _uiState.update { it.copy(releaseDate = value) }
    }

    fun saveSong() {

        val state = _uiState.value
        if (state.title.isBlank() || state.selectedArtistId == null || state.audioPath == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val song = Song(
                id = songId ?: 0,
                title = state.title,
                artistId = state.selectedArtistId,
                albumId = albumId,
                genre = state.genre,
                durationMs = state.duration,
                audioUrl = state.audioPath,
                imageUrl = state.imagePath,
                releaseDate = state.releaseDate
            )

            if(songId == null) {
                repository.insertSong(song)
            } else {
                repository.updateSong(song)
            }

            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }

        }
    }

    fun consumeSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    fun copyToInternalStorage(
        context: Context,
        uri: Uri,
        folder: String
    ): String {

        val input = context.contentResolver.openInputStream(uri)!!
        val dir = File(context.filesDir, folder)

        if(!dir.exists()) dir.mkdirs()

        val extension = uri.lastPathSegment
            ?.substringAfterLast(".", "mp3")

        val file = File(dir, "${System.currentTimeMillis()}.$extension")

        input.use { inputStream ->
            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }
        }
        return file.absolutePath
    }

    fun playPreview() {
        val path = _uiState.value.audioPath ?: return
        val file = File(path)

        if(!file.exists()) {
            Log.e("AddSongViewModel", "File not found: $path")
            return
        }

        mediaPlayer?.release()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(path)
            prepare()
            start()
        }

        _uiState.update { it.copy(isPlaying = true) }

        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                _uiState.update {
                    it.copy(
                        currentPosition = mediaPlayer!!.currentPosition.toLong()
                    )
                }

                delay(500)
            }
        }

    }

    fun stopPreview() {
        mediaPlayer?.stop()
        mediaPlayer = null

        progressJob?.cancel()

        _uiState.update {
            it.copy(
                currentPosition = 0,
                isPlaying = false
            )
        }
    }

    fun seekTo(position: Long) {
        mediaPlayer?.seekTo(position.toInt())

        _uiState.update {
            it.copy(currentPosition = position)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }

}

data class AddSongUiState(
    val artistQuery: String = "",
    val selectedArtistId: Long? = null,
    val artistSuggestions: List<Artist> = emptyList(),

    val title: String = "",
    val duration: Long = 0L,
    val currentPosition: Long = 0L,
    val isPlaying: Boolean = false,

    val genre: String = "",
    val audioPath: String? = null,
    val imagePath: String? = null,

    val releaseDate: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

