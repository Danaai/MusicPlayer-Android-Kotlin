package com.example.musicapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Album
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.MusicRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/* =======================================================
   ARTIST DETAIL
   ======================================================= */

class ArtistDetailViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArtistDetailUiState())
    val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()

    private var currentUserId: Long = -1

    fun loadArtist(artistId: Long, userId: Long) {

        currentUserId = userId

        viewModelScope.launch {

            val artist = repository.getArtistById(artistId)
                ?: return@launch

            combine(
                repository.getSongsByArtist(artistId),
                repository.getAlbumsByArtist(artistId),
                repository.observeIsFollowing(userId, artistId)
            ) { songs, albums, isFollowed ->

                ArtistDetailUiState(
                    artistId = artist.id,
                    artistName = artist.name,
                    artistImageUrl = artist.imageUrl,
                    artistInfo = artist.info,
                    songs = songs,
                    albums = albums,
                    isFollowed = isFollowed
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleFollow() {

        val artistId = _uiState.value.artistId ?: return
        val userId = currentUserId

        if(userId <= 0 ) return

        viewModelScope.launch {

            if(_uiState.value.isFollowed) {
                repository.unfollowArtist(userId, artistId)
            } else {
                repository.followArtist(userId, artistId)
            }
        }
    }
}

data class ArtistDetailUiState(
    val artistId: Long? = null,
    val artistName: String = "",
    val artistImageUrl: String? = null,
    val artistInfo: String? = null,
    val isFollowed: Boolean = false,
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList()
)

class AlbumDetailViewModel(
    private val musicRepository: MusicRepository,
    private val albumId: Long? = null,
) : ViewModel() {

    private val playingSongIdFlow = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<AlbumDetailUiState> = combine(

        musicRepository.getSongsByAlbum(albumId ?: -1),

        playingSongIdFlow
    ) { songs, playingSongId ->
        val album = musicRepository.getAlbumById(albumId ?: -1)
        val artist = album?.let {
            musicRepository.getArtistById(it.artistId)
        }

        AlbumDetailUiState(
            album = album,
            artist = artist,
            songs = songs,
            playingSongId = playingSongId,
            isLoading = false
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AlbumDetailUiState()
    )

    fun playSong(songId: Long) {
        playingSongIdFlow.value = songId
    }

}

data class AlbumDetailUiState(

    val album: Album? = null,
    val artist: Artist? = null,

    val songs: List<Song> = emptyList(),

    val playingSongId: Long? = null,

    val isLoading: Boolean = true,
)

/* =======================================================
   PLAYLIST DETAIL
   ======================================================= */

class PlaylistDetailViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistDetailUiState())
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {

            val playlist = repository.getPlaylistById(playlistId)
                ?: return@launch

            val artists = repository.getAllArtists()

            repository.getSongsByPlaylist(playlistId)
                .onEach { songs ->
                    _uiState.value = _uiState.value.copy(
                        playlistName = playlist.name,
                        ownerName = playlist.ownerName,
                        playlistImageUrl = playlist.imageUrl,
                        songs = songs
                    )
                }
                .launchIn(this)
        }
    }
}

data class PlaylistDetailUiState(
    val playlistName: String = "",
    val ownerName: String = "",
    val playlistImageUrl: String? = null,
    val songs: List<Song> = emptyList(),
    val artists: List<Artist> = emptyList()
)
