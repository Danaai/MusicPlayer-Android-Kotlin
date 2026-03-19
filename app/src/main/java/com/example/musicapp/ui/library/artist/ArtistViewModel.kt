package com.example.musicapp.ui.library.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.repository.MusicRepository
import com.example.musicapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ArtistSortType {
    FAVORITES,
    AZ,
    POPULARITY
}

class ArtistsViewModel(
    private val repository: MusicRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArtistsUiState())
    val uiState: StateFlow<ArtistsUiState> = _uiState.asStateFlow()

    private var allArtists: List<Artist> = emptyList()

    private var currentPage = 1
    private val pageSize = 10

    init {
        loadArtists()
    }

    private fun loadArtists() {

        viewModelScope.launch {

            userRepository.observeCurrentUser()
                .flatMapLatest { user ->

                    val userId = user?.id ?: return@flatMapLatest flowOf(
                        Pair(emptyList<Artist>(), emptySet<Long>())
                    )

                    combine(
                        repository.getAllArtists(),
                        repository.getFollowedArtistIds(userId)
                    ) { artists, followed ->

                        val followedIds = followed.map { it.artistId }.toSet()
                        Pair(artists, followedIds)
                    }
                }
                .collect { (artists, followedIds) ->

                    allArtists = artists

                    _uiState.value = _uiState.value.copy(
                        followedArtistIds = followedIds
                    )

                    applyFilter(allArtists, followedIds)
                }
        }
    }

    private fun applyFilter(
        artists: List<Artist>,
        followedIds: Set<Long>
    ) {

        var result = artists

        val query = _uiState.value.query
        if (query.isNotEmpty()) {
            result = result.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }

        result = when (_uiState.value.sortType) {

            ArtistSortType.AZ -> result.sortedBy { it.name }

            ArtistSortType.FAVORITES -> result.filter { followedIds.contains(it.id) }

            ArtistSortType.POPULARITY -> result.sortedByDescending { it.popularity }
        }

        val totalPages = if (result.isEmpty()) 1
        else (result.size + pageSize - 1) / pageSize

        val safePage = currentPage.coerceIn(1, totalPages)

        val pagedArtists = result
            .drop((safePage - 1) * pageSize)
            .take(pageSize)

        _uiState.value = _uiState.value.copy(
            artists = pagedArtists,
            currentPage = safePage,
            totalPages = totalPages
        )
    }

    fun onSearch(query: String) {

        currentPage = 1

        _uiState.value = _uiState.value.copy(query = query)

        applyFilter(
            allArtists,
            _uiState.value.followedArtistIds
        )
    }

    fun setSort(type: ArtistSortType) {

        currentPage = 1

        _uiState.value = _uiState.value.copy(sortType = type)

        applyFilter(
            allArtists,
            _uiState.value.followedArtistIds
        )
    }

    fun toggleFollowArtist(artistId: Long) {

        viewModelScope.launch {

            val user = userRepository.observeCurrentUser().first()
            val userId = user?.id ?: return@launch

            val isFollowing = _uiState.value.followedArtistIds.contains(artistId)

            if (isFollowing) {
                repository.unfollowArtist(userId, artistId)
            } else {
                repository.followArtist(userId, artistId)
            }
        }
    }

    fun nextPage() {
        currentPage++
        applyFilter(allArtists, _uiState.value.followedArtistIds)
    }

    fun prevPage() {
        currentPage = (currentPage - 1).coerceAtLeast(1)
        applyFilter(allArtists, _uiState.value.followedArtistIds)
    }

    fun goToPage(page: Int) {
        currentPage = page
        applyFilter(allArtists, _uiState.value.followedArtistIds)
    }
}

data class ArtistsUiState(
    val artists: List<Artist> = emptyList(),
    val followedArtistIds: Set<Long> = emptySet(),
    val query: String = "",
    val sortType: ArtistSortType = ArtistSortType.AZ,

    val currentPage: Int = 1,
    val totalPages: Int = 1
)