package com.example.musicapp.ui.admin.album_manangement

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Album
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumManagementViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _deleteTarget = MutableStateFlow<Album?>(null)
    private val _currentPage = MutableStateFlow(1)

    val uiState: StateFlow<AlbumManagementUiState> =
        combine(
            repository.getAllAlbums(),
            repository.getAllArtists(),
            _searchQuery,
            _deleteTarget,
            _currentPage
        ) { albums, artists, query, deleteTarget, page ->

            val artistMap = artists.associateBy { it.id }

            val mappedAlbums = albums.map { album ->
                album to (artistMap[album.artistId]?.name ?: "Unknown")
            }

            val filtered = if (query.isBlank()) mappedAlbums
            else mappedAlbums.filter { it.first.title.contains(query, true) }

            val pageSize = 8
            val totalPages = (filtered.size + pageSize - 1) / pageSize

            val safePage = page.coerceIn(1, totalPages.coerceAtLeast(1))

            val pagedAlbums = filtered
                .drop((safePage - 1)*pageSize)
                .take(pageSize)

            AlbumManagementUiState(
                albums = pagedAlbums,
                searchQuery = query,
                showDeleteDialog = deleteTarget != null,
                albumToDelete = deleteTarget,
                currentPage = safePage,
                totalPages = totalPages
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AlbumManagementUiState()
        )

    fun onSearchChange(value: String) {
        _searchQuery.value = value
    }

    fun onDeleteClick(album: Album) {
        _deleteTarget.value = album
    }

    fun dismissDeleteDialog() {
        _deleteTarget.value = null
    }

    fun confirmDelete() {
        viewModelScope.launch {
            _deleteTarget.value?.let {
                repository.deleteAlbum(it.id)
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

data class AlbumManagementUiState(
    val albums: List<Pair<Album, String>> = emptyList(),
    val searchQuery: String = "",
    val showDeleteDialog: Boolean = false,
    val albumToDelete: Album? = null,

    val currentPage: Int = 1,
    val totalPages: Int = 1
)

class AddAlbumViewModel(
    private val repository: MusicRepository,
    private val albumId: Long?,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddAlbumUiState())
    val uiState = _uiState.asStateFlow()

    private var allArtists: List<Artist> = emptyList()

    init {
        observeSongs()
        observeArtists()

        albumId?.let { loadAlbum(it) }
    }

    private fun loadAlbum(id: Long) {
        viewModelScope.launch {
            repository.getAlbumById(id)?. let { album ->

                val artist = repository.getArtistById(album.artistId)

                _uiState.update {
                    it.copy(
                        title = album.title,
                        artistQuery = "${artist?.id} - ${artist?.name}",
                        selectedArtistId = album.artistId,
                        releaseDate = album.releaseDate,
                        imageUrl = album.imageUrl,
                    )
                }
            }
        }
    }

    private fun observeSongs() {
        val id = albumId ?: return

        viewModelScope.launch {
            repository.getSongsByAlbum(id).collect { songs ->
                _uiState.update {
                    it.copy(songs = songs)
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
        _uiState.update {
            it.copy(title = value)
        }
    }

    fun onReleaseDateChange(value: String) {
        _uiState.update {
            it.copy(releaseDate = value)
        }
    }

    fun onImageSelected(path: String) {
        _uiState.update {
            it.copy(imageUrl = path)
        }
    }

    fun saveAlbum() {

        val state = _uiState.value

        viewModelScope.launch {

            _uiState.update { it.copy(isSaving = true) }

            val album = Album(
                id = albumId ?: 0,
                title = state.title,
                artistId = state.selectedArtistId!!,
                releaseDate = state.releaseDate,
                imageUrl = state.imageUrl
            )

            if(albumId == null) {
                repository.insertAlbum(album)
            } else {
                repository.updateAlbum(album)
            }

            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }

    fun consumeSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    fun onDeleteClick(song: Song) {
        _uiState.update {
            it.copy(
                showDeleteDialog = true,
                songToDelete = song
            )
        }
    }

    fun dismissDeleteDialog() {
        _uiState.update {
            it.copy(
                showDeleteDialog = false,
                songToDelete = null
            )
        }
    }

    fun confirmDelete() {
        viewModelScope.launch {

            val song = _uiState.value.songToDelete ?: return@launch

            repository.updateSong(
                song.copy(albumId = null)
            )

            _uiState.update {
                it.copy(
                    showDeleteDialog = false,
                    songToDelete = null
                )
            }

        }
    }
}

data class AddAlbumUiState(
    val songs: List<Song> = emptyList(),

    val artistQuery: String = "",
    val selectedArtistId: Long? = null,
    val artistSuggestions: List<Artist> = emptyList(),

    val title: String = "",
    val releaseDate: String? = null,
    val imageUrl: String? = null,

    val showDeleteDialog: Boolean = false,
    val songToDelete: Song? = null,

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

class AddSongToAlbumViewModel(
    private val repository: MusicRepository,
    private val albumId: Long?
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedSongs = MutableStateFlow<Set<Long>>(emptySet())

    init {
        loadSelectedSongs()
    }

    val uiState: StateFlow<AddSongToAlbumUiState> =
        combine(
            repository.getAllSongs(),
            _searchQuery,
            _selectedSongs
        ) { songs, query, selected ->

            val filtered =
                if (query.isBlank()) songs
                else songs.filter {
                    it.title.contains(query, ignoreCase = true)
                }

            AddSongToAlbumUiState(
                songs = filtered,
                selectedSongs = selected,
                searchQuery = query
            )

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AddSongToAlbumUiState()
        )

    private fun loadSelectedSongs() {
        viewModelScope.launch {
            albumId?.let { id ->
                repository.getSongsByAlbum(id).collect { songs ->
                    _selectedSongs.value = songs.map { it.id }.toSet()
                }
            }
        }
    }

    fun onSearchChange(value: String) {
        _searchQuery.value = value
    }

    fun toggleSong(songId: Long) {

        val current = _selectedSongs.value.toMutableSet()

        if (current.contains(songId)) {
            current.remove(songId)
        } else {
            current.add(songId)
        }

        _selectedSongs.value = current
    }

    fun saveSongs() {

        viewModelScope.launch {

            val allSongs = repository.getAllSongs().first()

            val selectedIds = _selectedSongs.value

            allSongs.forEach { song ->

                when {
                    song.id in selectedIds -> {
                        if(song.albumId != albumId) {
                            repository.updateSong(
                                song.copy(albumId = albumId)
                            )
                        }
                    }

                    song.albumId == albumId -> {
                        repository.updateSong(
                            song.copy(albumId = null)
                        )
                    }
                }

            }

        }

    }
}

data class AddSongToAlbumUiState(
    val songs: List<Song> = emptyList(),
    val selectedSongs: Set<Long> = emptySet(),
    val searchQuery: String = "",
)