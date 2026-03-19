package com.example.musicapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import com.example.musicapp.domain.model.Album
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.MusicRepository
import com.example.musicapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val musicRepository: MusicRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")
    private val recentSearchFlow = MutableStateFlow<List<String>>(emptyList())
    private val currentUserFlow = userRepository.observeCurrentUser()

    val uiState: StateFlow<HomeUiState> =
        combine(
            queryFlow,
            queryFlow.flatMapLatest { query -> search(query) },
            recentSearchFlow,
            currentUserFlow
        ) { query, partial, recent, user ->

            HomeUiState(
                isLoggedIn = user != null,
                avatarUrl = user?.avtUrl,
                query = query,
                songs = partial.songs,
                artists = partial.artists,
                albums = partial.albums,
                playlists = partial.playlists.filter { playlist ->
                    playlist.isPublic && playlist.ownerId != user?.id
                },
                recentSearches = recent,
                isSearching = query.isNotBlank(),
                isEmpty = query.isNotBlank() &&
                        partial.songs.isEmpty() &&
                        partial.artists.isEmpty() &&
                        partial.albums.isEmpty() &&
                        partial.playlists.isEmpty()
            )
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                HomeUiState()
            )

    private fun search(query: String) =

        if(query.isBlank()) {

            combine(
                musicRepository.getAllSongs(),
                musicRepository.getAllArtists(),
                musicRepository.getAllAlbums(),
                musicRepository.getAllPlaylists()
            ) { songs, artists, albums, playlists ->

                SearchPartialResult(
                    songs = songs,
                    artists = artists,
                    albums = albums,
                    playlists = playlists
                )
            }
        } else {
            combine(
                musicRepository.searchSongs(query),
                musicRepository.searchArtists(query),
                musicRepository.getAllArtists(),
                musicRepository.searchAlbums(query),
                musicRepository.searchPlaylists(query)
            ) { songs, searchedArtists, allArtists, albums, playlists ->

                val songArtists = allArtists.filter { artist ->
                    songs.any { it.artistId == artist.id }
                }

                val artists = (searchedArtists + songArtists)
                    .distinctBy { it.id }

                SearchPartialResult(
                    songs = songs,
                    artists = artists,
                    albums = albums,
                    playlists = playlists
                )
            }
        }

    fun onQueryChange(query: String) {
        queryFlow.value = query
    }

    fun onClearQuery() {
        queryFlow.value = ""
    }

    fun onSearchSubmit() {
        val query = queryFlow.value.trim()
        if (query.isBlank()) return

        recentSearchFlow.update { old ->
            (listOf(query) + old)
                .distinct()
                .take(10)
        }
    }

    fun onClickRecent(keyword: String) {
        queryFlow.value = keyword
    }

    fun removeRecent(keyword: String) {
        recentSearchFlow.update { it - keyword }
    }

    fun onClearRecent() {
        recentSearchFlow.value = emptyList()
    }
}

private data class SearchPartialResult(
    val songs: List<Song>,
    val artists: List<Artist>,
    val albums: List<Album>,
    val playlists: List<Playlist>
)

data class HomeUiState(
    val isLoggedIn: Boolean = false,
    val avatarUrl: String? = null,

    val query: String = "",
    val recentSearches: List<String> = emptyList(),

    val songs: List<Song> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Album> = emptyList(),
    val playlists: List<Playlist> = emptyList(),

    val isSearching: Boolean = false,
    val isEmpty: Boolean = false
)