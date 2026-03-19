package com.example.musicapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicapp.data.db.AppDatabase
import com.example.musicapp.data.repository.*
import com.example.musicapp.ui.admin.AdminViewModel
import com.example.musicapp.ui.admin.album_manangement.AddAlbumViewModel
import com.example.musicapp.ui.admin.album_manangement.AddSongToAlbumViewModel
import com.example.musicapp.ui.admin.album_manangement.AlbumManagementViewModel
import com.example.musicapp.ui.admin.artist_management.AddArtistViewModel
import com.example.musicapp.ui.admin.artist_management.ArtistManagementViewModel
import com.example.musicapp.ui.admin.song_management.AddSongViewModel
import com.example.musicapp.ui.admin.song_management.SongManagementViewModel
import com.example.musicapp.ui.admin.user_management.AddUserViewModel
import com.example.musicapp.ui.admin.user_management.UserManagementViewModel
import com.example.musicapp.ui.auth.AuthViewModel
import com.example.musicapp.ui.detail.AlbumDetailViewModel
import com.example.musicapp.ui.login_out.login.LoginViewModel
import com.example.musicapp.ui.login_out.register.RegisterViewModel
import com.example.musicapp.ui.detail.ArtistDetailViewModel
import com.example.musicapp.ui.detail.PlaylistDetailViewModel
import com.example.musicapp.ui.editProfile.EditProfileViewModel
import com.example.musicapp.ui.favorites_history.SongCollectionViewModel
import com.example.musicapp.ui.home.HomeViewModel
import com.example.musicapp.ui.library.LibraryViewModel
import com.example.musicapp.ui.library.album.AlbumsViewModel
import com.example.musicapp.ui.library.artist.ArtistsViewModel
import com.example.musicapp.ui.library.playlist.AddPlaylistViewModel
import com.example.musicapp.ui.library.playlist.AddSongToPlaylistViewModel
import com.example.musicapp.ui.library.playlist.PlaylistManagementViewModel
import com.example.musicapp.ui.library.song.SongsViewModel
import com.example.musicapp.ui.player.PlayerViewModel
import com.example.musicapp.ui.settings.SettingsViewModel

class AppViewModelFactory(
    private val context: Context,
    private val artistId: Long? = null,
    private val songId: Long? = null,
    private val albumId: Long? = null,
    private val userId: Long? = null,
    private val playlistId: Long? = null
) : ViewModelProvider.Factory {

    private val database by lazy {
        AppDatabase.getInstance(context.applicationContext)
    }

    private val songDao by lazy { database.songDao() }
    private val artistDao by lazy { database.artistDao() }
    private val playlistDao by lazy { database.playlistDao() }
    private val userDao by lazy { database.userDao() }
    private val albumDao by lazy { database.albumDao() }

    private val musicRepository by lazy {
        MusicRepositoryImpl(songDao, artistDao, playlistDao, userDao, albumDao)
    }

    private val userRepository by lazy {
        UserRepositoryImpl(userDao)
    }

    private val authRepository by lazy {
        AuthRepositoryImpl(userDao)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(authRepository) as T

            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(authRepository) as T

            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(musicRepository, userRepository) as T

            modelClass.isAssignableFrom(PlaylistDetailViewModel::class.java) ->
                PlaylistDetailViewModel(musicRepository) as T

            modelClass.isAssignableFrom(ArtistDetailViewModel::class.java) ->
                ArtistDetailViewModel(musicRepository) as T

            modelClass.isAssignableFrom(AlbumDetailViewModel::class.java) ->
                AlbumDetailViewModel(musicRepository, albumId) as T

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(userRepository) as T

            modelClass.isAssignableFrom(EditProfileViewModel::class.java) ->
                EditProfileViewModel(userRepository) as T

            modelClass.isAssignableFrom(SongCollectionViewModel::class.java) ->
                SongCollectionViewModel( userRepository, musicRepository) as T

            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(userRepository) as T

            modelClass.isAssignableFrom(PlayerViewModel::class.java) ->
                PlayerViewModel(context.applicationContext as Application,musicRepository, authRepository) as T

            modelClass.isAssignableFrom(AdminViewModel::class.java) ->
                AdminViewModel(musicRepository, userRepository) as T

            modelClass.isAssignableFrom(SongManagementViewModel::class.java) ->
                SongManagementViewModel(musicRepository) as T

            modelClass.isAssignableFrom(AddSongViewModel::class.java) ->
                AddSongViewModel(musicRepository, songId, context.applicationContext) as T

            modelClass.isAssignableFrom(ArtistManagementViewModel::class.java) ->
                ArtistManagementViewModel(musicRepository) as T

            modelClass.isAssignableFrom(AddArtistViewModel::class.java) ->
                AddArtistViewModel(musicRepository, artistId) as T

            modelClass.isAssignableFrom(AlbumManagementViewModel::class.java) ->
                AlbumManagementViewModel(musicRepository) as T

            modelClass.isAssignableFrom(AddAlbumViewModel::class.java) ->
                AddAlbumViewModel(musicRepository, albumId, context.applicationContext) as T

            modelClass.isAssignableFrom(AddSongToAlbumViewModel::class.java) ->
                AddSongToAlbumViewModel(musicRepository, albumId) as T

            modelClass.isAssignableFrom(UserManagementViewModel::class.java) ->
                UserManagementViewModel(userRepository) as T

            modelClass.isAssignableFrom(AddUserViewModel::class.java) ->
                AddUserViewModel(userRepository, userId) as T

            modelClass.isAssignableFrom(LibraryViewModel::class.java) ->
                LibraryViewModel(userRepository) as T

            modelClass.isAssignableFrom(PlaylistManagementViewModel::class.java) ->
                PlaylistManagementViewModel(musicRepository, userRepository) as T

            modelClass.isAssignableFrom(AddPlaylistViewModel::class.java) ->
                AddPlaylistViewModel(musicRepository, userRepository, playlistId) as T

            modelClass.isAssignableFrom(AddSongToPlaylistViewModel::class.java) ->
                AddSongToPlaylistViewModel(musicRepository, playlistId) as T

            modelClass.isAssignableFrom(ArtistsViewModel::class.java) ->
                ArtistsViewModel(musicRepository, userRepository) as T

            modelClass.isAssignableFrom(AlbumsViewModel::class.java) ->
                AlbumsViewModel(musicRepository) as T

            modelClass.isAssignableFrom(SongsViewModel::class.java) ->
                SongsViewModel(musicRepository, userRepository) as T

            else -> throw IllegalArgumentException(
                "Unknown ViewModel class: ${modelClass.name}"
            )
        }
    }
}