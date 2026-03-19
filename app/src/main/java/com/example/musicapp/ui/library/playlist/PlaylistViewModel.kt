package com.example.musicapp.ui.library.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.model.User
import com.example.musicapp.domain.repository.MusicRepository
import com.example.musicapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PlaylistSortType{
    MY_PLAYLISTS,
    PUBLIC_PLAYLISTS
}

class PlaylistManagementViewModel(
    private val repository: MusicRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _deleteTarget = MutableStateFlow<Playlist?>(null)
    private val _sortType = MutableStateFlow(PlaylistSortType.MY_PLAYLISTS)

    private val currentUserId =
        userRepository.observeCurrentUser()
            .map { it?.id }

    val uiState: StateFlow<PlaylistManagementUiState> =
        combine(
            repository.getAllPlaylists(),
            _searchQuery,
            _deleteTarget,
            currentUserId,
            _sortType
        ) { playlists, query, deleteTarget, userId, sortType ->

            val filteredBySearch = if (query.isBlank()) playlists
            else playlists.filter { it.name.contains(query, true) }

            val finalList = when (sortType) {

                PlaylistSortType.MY_PLAYLISTS ->
                    filteredBySearch.filter { it.ownerId == userId }

                PlaylistSortType.PUBLIC_PLAYLISTS ->
                    filteredBySearch.filter {
                        it.isPublic && it.ownerId != userId
                    }

            }

            PlaylistManagementUiState(
                currentUserId = userId,
                playlists = finalList,
                query = query,
                sortType = sortType,
                showDeleteDialog = deleteTarget != null,
                playlistToDelete = deleteTarget
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PlaylistManagementUiState()
        )

    val myPlaylistCount: StateFlow<Int> =
        combine(
            repository.getAllPlaylists(),
            currentUserId
        ) { playlists, userId ->
            playlists.count { it.ownerId == userId }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    fun onSearchChange(value: String) {
        _searchQuery.value = value
    }

    fun onDeleteClick(playlist: Playlist) {
        _deleteTarget.value = playlist
    }

    fun dismissDeleteDialog() {
        _deleteTarget.value = null
    }

    fun confirmDelete() {
        viewModelScope.launch {
            _deleteTarget.value?.let {
                repository.deletePlaylist(it.id)
            }
            _deleteTarget.value = null
        }
    }

    fun setSort(sortType: PlaylistSortType) {
        _sortType.value = sortType
    }

    fun canCreatePlaylist(count: Int): Boolean {
        return count < 10
    }
}

data class PlaylistManagementUiState(
    val currentUserId: Long? = null,
    val playlists: List<Playlist> = emptyList(),
    val query: String = "",
    val sortType: PlaylistSortType = PlaylistSortType.MY_PLAYLISTS,
    val showDeleteDialog: Boolean = false,
    val playlistToDelete: Playlist? = null,
)

class AddPlaylistViewModel(
    private val repository: MusicRepository,
    private val userRepository: UserRepository,
    private val playlistId: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPlaylistUiState())
    val uiState = _uiState.asStateFlow()

    private var currentUser: User? = null

    init {

        viewModelScope.launch {
            userRepository.observeCurrentUser().collect {
                currentUser = it
            }
        }

        observeSongs()

        playlistId?.let { loadPlaylist(it) }
    }

    private fun loadPlaylist(id: Long) {
        viewModelScope.launch {

            repository.getPlaylistById(id)?. let { playlist ->

                _uiState.update {
                    it.copy(
                        name = playlist.name,
                        description = playlist.description,
                        imageUrl = playlist.imageUrl,
                        isPublic = playlist.isPublic,
                        createdAt = playlist.createdAt
                    )
                }
            }
        }
    }

    private fun observeSongs() {
        val id = playlistId ?: return

        viewModelScope.launch {
            repository.getSongsByPlaylist(id).collect { songs ->
                _uiState.update {
                    it.copy(songs = songs)
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update {
            it.copy(name = value)
        }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update {
            it.copy(description = value)
        }
    }

    fun onPublicChange(value: Boolean) {
        _uiState.update {
            it.copy(isPublic = value)
        }
    }

    fun onImageSelected(path: String) {
        _uiState.update {
            it.copy(imageUrl = path)
        }
    }

    fun savePlaylist() {

        val state = _uiState.value
        val user = currentUser ?: return

        viewModelScope.launch {

            _uiState.update { it.copy(isSaving = true) }

            val playlist = Playlist(
                id = playlistId ?: 0,
                name = state.name,
                description = state.description,
                ownerId = user.id,
                ownerName = user.username,
                imageUrl = state.imageUrl,
                isPublic = state.isPublic,
                createdAt = state.createdAt
            )

            if (playlistId == null) {
                repository.insertPlaylist(playlist)
            } else {
                repository.updatePlaylist(playlist)
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
            val playlist = playlistId ?: return@launch

            repository.removeSongFromPlaylist(song.id, playlist)

            _uiState.update {
                it.copy(
                    showDeleteDialog = false,
                    songToDelete = null
                )
            }

        }
    }
}

data class AddPlaylistUiState(
    val songs: List<Song> = emptyList(),

    val name: String = "",
    val description: String? = null,
    val imageUrl: String? = null,
    val isPublic: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),

    val showDeleteDialog: Boolean = false,
    val songToDelete: Song? = null,

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

class AddSongToPlaylistViewModel(
    private val repository: MusicRepository,
    private val playlistId: Long?
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedSongs = MutableStateFlow<Set<Long>>(emptySet())

    init {
        loadSelectedSongs()
    }

    val uiState: StateFlow<AddSongToPlaylistUiState> =
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

            AddSongToPlaylistUiState(
                songs = filtered,
                selectedSongs = selected,
                searchQuery = query
            )

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AddSongToPlaylistUiState()
        )

    private fun loadSelectedSongs() {
        viewModelScope.launch {
            playlistId?.let { id ->
                repository.getSongsByPlaylist(id).collect { songs ->
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

            val currentSongs = repository.getSongsByPlaylist(playlistId ?: return@launch).first().map { it.id }.toSet()

            val selected = _selectedSongs.value

            val toAdd = selected - currentSongs
            val toRemove = currentSongs - selected

            toAdd.forEach {
                repository.addSongToPlaylist(it, playlistId)
            }

            toRemove.forEach {
                repository.removeSongFromPlaylist(it, playlistId)
            }

        }

    }
}

data class AddSongToPlaylistUiState(
    val songs: List<Song> = emptyList(),
    val selectedSongs: Set<Long> = emptySet(),
    val searchQuery: String = "",
)