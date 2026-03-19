package com.example.musicapp.navigation

import com.example.musicapp.ui.favorites_history.SongCollectionType
import com.example.musicapp.ui.library.LibraryTab

sealed class AppDestination(val route: String) {

    object Login: AppDestination("login")
    object Register: AppDestination("register")

    object Main: AppDestination("main")
    object Admin: AppDestination("admin")
    object Splash: AppDestination("splash")

    object Home: AppDestination("home")
    object Library : AppDestination("library")
    object LibraryTabs : AppDestination("library?tab={tab}") {
        fun createRoute(tab: LibraryTab) = "library?tab=${tab.name}"
    }
    object Settings: AppDestination("settings")

    object NowPlaying: AppDestination("now_playing")

    object Notifications: AppDestination("notifications")
    object Help: AppDestination("help")
    object About: AppDestination("about")
    object EditProfile: AppDestination("edit_profile")

    object SongCollection : AppDestination("songs/{type}") {
        fun createRoute(type: SongCollectionType) =
            "songs/${type.name}"
    }

    object ArtistDetail: AppDestination("artist/{artistId}") {
        fun createRoute(artistId: Long) = "artist/$artistId"
    }
    object PlaylistDetail: AppDestination("playlist/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist/$playlistId"
    }
    object AlbumDetail: AppDestination("album/{albumId}") {
        fun createRoute(albumId: Long) = "album/$albumId"
    }

    object AddPlaylist : AppDestination("add_playlist")
    object EditPlaylist : AppDestination("add_playlist/{playlistId}") {
        fun createRoute(playlistId: Long) = "add_playlist/$playlistId"
    }
    object AddSongToPlaylist : AppDestination("add_song_to_playlist/{playlistId}") {
        fun createRoute(playlistId: Long) = "add_song_to_playlist/$playlistId"
    }

    object SongManagement : AppDestination("admin/songs")
    object AddSong : AppDestination("admin/add_song")
    object EditSong: AppDestination("admin/add_song/{songId}") {
        fun createRoute(songId: Long) = "admin/add_song/$songId"
    }

    object ArtistManagement : AppDestination("admin/artists")
    object AddArtist : AppDestination("admin/add_artist")
    object EditArtist : AppDestination("admin/add_artist/{artistId}") {
        fun createRoute(artistId: Long) = "admin/add_artist/$artistId"
    }

    object AlbumManagement : AppDestination("admin/albums")
    object AddAlbum : AppDestination("admin/add_album")
    object EditAlbum : AppDestination("admin/add_album/{albumId}") {
        fun createRoute(albumId: Long) = "admin/add_album/$albumId"
    }
    object AddSongToAlbum : AppDestination("admin/add_song_to_album/{albumId}") {
        fun createRoute(albumId: Long) = "admin/add_song_to_album/$albumId"
    }

    object UserManagement : AppDestination("admin/users")
    object AddUser : AppDestination("admin/add_user")
    object EditUser : AppDestination("admin/add_user/{userId}") {
        fun createRoute(userId: Long) = "admin/add_user/$userId"
    }

}