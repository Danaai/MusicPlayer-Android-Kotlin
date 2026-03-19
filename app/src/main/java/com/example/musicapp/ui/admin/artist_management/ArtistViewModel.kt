package com.example.musicapp.ui.admin.artist_management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArtistManagementViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _deleteTarget = MutableStateFlow<Artist?>(null)
    private val _currentPage = MutableStateFlow(1)

    val uiState: StateFlow<ArtistManagementUiState> =
        combine(
            repository.getAllArtists(),
            _searchQuery,
            _deleteTarget,
            _currentPage
        ) { artists, query, deleteTarget, page ->

            val filtered = if (query.isBlank()) artists
            else artists.filter { it.name.contains(query, true) }

            val pageSize = 8
            val totalPages = (filtered.size + pageSize - 1 ) / pageSize

            val safePage = page.coerceIn(1, totalPages.coerceAtLeast(1))

            val pagedArtists = filtered
                .drop((safePage - 1)*pageSize)
                .take(pageSize)

            ArtistManagementUiState(
                artists = pagedArtists,
                searchQuery = query,
                showDeleteDialog = deleteTarget != null,
                artistToDelete = deleteTarget,
                currentPage = safePage,
                totalPages = totalPages
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ArtistManagementUiState()
        )

    fun onSearchChange(value: String) {
        _searchQuery.value = value
    }

    fun onDeleteClick(artist: Artist) {
        _deleteTarget.value = artist
    }

    fun dismissDeleteDialog() {
        _deleteTarget.value = null
    }

    fun confirmDelete() {
        viewModelScope.launch {
            _deleteTarget.value?.let {
                repository.deleteArtist(it.id)
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

data class ArtistManagementUiState(
    val artists: List<Artist> = emptyList(),
    val searchQuery: String = "",
    val showDeleteDialog: Boolean = false,
    val artistToDelete: Artist? = null,

    val currentPage: Int = 1,
    val totalPages: Int = 1
)

class AddArtistViewModel(
    private val repository: MusicRepository,
    private val artistId: Long? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddArtistUiState())
    val uiState: StateFlow<AddArtistUiState> = _uiState.asStateFlow()

    init {
        artistId?.let { loadArtist(it) }
    }

    private fun loadArtist(id: Long) {
        viewModelScope.launch {
            repository.getArtistById(id)?.let { artist ->
                _uiState.value = AddArtistUiState(
                    name = artist.name,
                    info = artist.info ?: "",
                    imageUrl = artist.imageUrl,
                    popularity = artist.popularity.toFloat()
                )
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onImageSelected(value: String) {
        _uiState.update { it.copy(imageUrl = value) }
    }

    fun onInfoChange(value: String) {
        _uiState.update { it.copy(info = value) }
    }

    fun onPopularityChange(value: Float) {
        _uiState.update { it.copy(popularity = value) }
    }

    fun saveArtist() {
        viewModelScope.launch {

            val state = _uiState.value
            _uiState.update { it.copy(isSaving = true) }

            val artist = Artist(
                id = artistId ?: 0,
                name = state.name,
                info = state.info,
                imageUrl = state.imageUrl,
                popularity = state.popularity.toInt()
            )

            if(artistId == null) {
                repository.insertArtist(artist)
            } else {
                repository.updateArtist(artist)
            }

            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }

    fun consumeSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

}

data class AddArtistUiState(
    val name: String = "",
    val info: String = "",
    val imageUrl: String? = null,
    val popularity: Float = 50f,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)