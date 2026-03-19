package com.example.musicapp.ui.library.song

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.MusicRepository
import com.example.musicapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

enum class SongSortType {
    LATEST,
    FAVORITES
}

class SongsViewModel(
    private val musicRepository: MusicRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SongsUiState())
    val uiState: StateFlow<SongsUiState> = _uiState.asStateFlow()

    private var allSongs: List<Song> = emptyList()

    private var currentPage = 1
    private val pageSize = 10

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {

            userRepository.observeCurrentUser()
                .flatMapLatest { user ->

                    val userId = user?.id ?: return@flatMapLatest flowOf(
                        Triple(emptyList<Song>(), emptySet<Long>(), emptyMap<Long, String>())
                    )

                    combine(
                        musicRepository.getAllSongs(),
                        musicRepository.getFavoriteSongIds(userId),
                        musicRepository.getAllArtists()
                    ) { songs, favorites, artists ->

                        val favoriteIds = favorites.map { it.songId }.toSet()

                        val artistMap = artists.associate {
                            it.id to it.name
                        }

                        Triple(songs, favoriteIds, artistMap)

                    }
                }
                .collect { (songs, favoriteIds, artistMap) ->

                    allSongs = songs

                    _uiState.value = _uiState.value.copy(
                        favoriteIds = favoriteIds,
                        artistMap = artistMap
                    )

                    applyFilter(allSongs, favoriteIds)

                }
        }
    }

    private fun applyFilter(
        songs: List<Song>,
        favoriteIds: Set<Long>
    ) {

        var result = songs

        val query = _uiState.value.query
        if (query.isNotEmpty()) {
            result = result.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }

        result = when (_uiState.value.sortType) {

            SongSortType.LATEST -> result.sortedByDescending { it.releaseDate }

            SongSortType.FAVORITES -> result.filter { favoriteIds.contains(it.id) }
        }

        val totalPages = if (result.isEmpty()) 1
        else (result.size + pageSize - 1) / pageSize

        val safePage = currentPage.coerceIn(1, totalPages)

        val pagedSongs = result
            .drop((safePage - 1) * pageSize)
            .take(pageSize)

        _uiState.value = _uiState.value.copy(
            songs = pagedSongs,
            currentPage = safePage,
            totalPages = totalPages
        )
    }

    fun onSearch(query: String) {

        currentPage = 1

        _uiState.value = _uiState.value.copy(query = query)

        applyFilter(
            allSongs,
            _uiState.value.favoriteIds
        )
    }

    fun setSort(type: SongSortType) {

        currentPage = 1

        _uiState.value = _uiState.value.copy(sortType = type)

        applyFilter(
            allSongs,
            _uiState.value.favoriteIds
        )
    }

    fun nextPage() {
        currentPage++
        applyFilter(allSongs, _uiState.value.favoriteIds)
    }

    fun prevPage() {
        currentPage = (currentPage - 1).coerceAtLeast(1)
        applyFilter(allSongs, _uiState.value.favoriteIds)
    }

    fun goToPage(page: Int) {
        currentPage = page
        applyFilter(allSongs, _uiState.value.favoriteIds)
    }
}

data class SongsUiState(
    val songs: List<Song> = emptyList(),
    val favoriteIds: Set<Long> = emptySet(),
    val artistMap: Map<Long, String> = emptyMap(),
    val query: String = "",
    val sortType: SongSortType = SongSortType.LATEST,

    val currentPage: Int = 1,
    val totalPages: Int = 1
)