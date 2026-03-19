package com.example.musicapp.ui.library.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Album
import com.example.musicapp.domain.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class AlbumSortType {
    AZ,
    LATEST
}

class AlbumsViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumsUiState())
    val uiState: StateFlow<AlbumsUiState> = _uiState.asStateFlow()

    private var allAlbums: List<Album> = emptyList()

    private var currentPage = 1
    private val pageSize = 6

    init {
        loadAlbums()
    }

    private fun loadAlbums() {
        viewModelScope.launch {
            repository.getAllAlbums().collectLatest { albums ->
                allAlbums = albums
                applyFilter()
            }
        }
    }

    fun onSearch(query: String) {
        currentPage = 1

        _uiState.value = _uiState.value.copy(query = query)

        applyFilter()
    }

    fun setSort(sortType: AlbumSortType) {
        currentPage = 1

        _uiState.value = _uiState.value.copy(sortType = sortType)

        applyFilter()
    }

    private fun applyFilter() {

        var filtered = allAlbums

        val query = _uiState.value.query

        if(query.isNotEmpty()) {
            filtered = filtered.filter {
                it.title.contains(query, true)
            }
        }

        filtered = when (_uiState.value.sortType) {
            AlbumSortType.AZ -> filtered.sortedBy { it.title }

            AlbumSortType.LATEST -> filtered.sortedByDescending { it.releaseDate }
        }

        val totalPages = if (filtered.isEmpty()) 1
        else (filtered.size + pageSize - 1) / pageSize

        val safePage = currentPage.coerceIn(1, totalPages)

        val pagedAlbums = filtered
            .drop((safePage - 1) * pageSize)
            .take(pageSize)

        _uiState.value = _uiState.value.copy(
            albums = pagedAlbums,
            currentPage = safePage,
            totalPages = totalPages
        )
    }

    fun nextPage() {
        currentPage++
        applyFilter()
    }

    fun prevPage() {
        currentPage = (currentPage - 1).coerceAtLeast(1)
        applyFilter()
    }

    fun goToPage(page: Int) {
        currentPage = page
        applyFilter()
    }
}

data class AlbumsUiState(
    val albums: List<Album> = emptyList(),
    val query: String = "",
    val sortType: AlbumSortType = AlbumSortType.AZ,

    val currentPage: Int = 1,
    val totalPages: Int = 1
)