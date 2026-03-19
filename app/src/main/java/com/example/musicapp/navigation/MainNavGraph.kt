package com.example.musicapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicapp.ui.auth.AuthState
import com.example.musicapp.ui.auth.AuthViewModel
import com.example.musicapp.ui.detail.AlbumDetailRoute
import com.example.musicapp.ui.detail.ArtistDetailRoute
import com.example.musicapp.ui.detail.PlaylistDetailRoute
import com.example.musicapp.ui.editProfile.EditProfileRoute
import com.example.musicapp.ui.favorites_history.SongCollectionRoute
import com.example.musicapp.ui.favorites_history.SongCollectionType
import com.example.musicapp.ui.helpnabout.AboutRoute
import com.example.musicapp.ui.helpnabout.HelpRoute
import com.example.musicapp.ui.home.HomeRoute
import com.example.musicapp.ui.library.LibraryRoute
import com.example.musicapp.ui.library.LibraryTab
import com.example.musicapp.ui.library.playlist.AddPlaylistRoute
import com.example.musicapp.ui.library.playlist.AddSongToPlaylistRoute
import com.example.musicapp.ui.player.NowPlayingRoute
import com.example.musicapp.ui.player.PlayerViewModel
import com.example.musicapp.ui.settings.SettingsRoute

fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController,
    playerViewModel: PlayerViewModel,
    authViewModel: AuthViewModel
) {
    composable(AppDestination.Home.route) {
        HomeRoute(
            playerViewModel = playerViewModel,
            onProfileClick = {
                navController.navigate(
                    AppDestination.EditProfile.route
                )
            },
            onPlaylistClick = { playlistId ->
                navController.navigate(
                    AppDestination.PlaylistDetail.createRoute(playlistId)
                )
            },
            onArtistClick = { artistId ->
                navController.navigate(
                    AppDestination.ArtistDetail.createRoute(artistId)
                )
            },
            onAlbumClick = { albumId ->
                navController.navigate(
                    AppDestination.AlbumDetail.createRoute(albumId)
                )
            },
            onSongsClick = {
                navController.navigate(
                    AppDestination.LibraryTabs.createRoute(LibraryTab.SONGS)
                )
            },
            onArtistsClick = {
                navController.navigate(
                    AppDestination.LibraryTabs.createRoute(LibraryTab.ARTISTS)
                )
            },
            onAlbumsClick = {
                navController.navigate(
                    AppDestination.LibraryTabs.createRoute(LibraryTab.ALBUMS)
                )
            },
            onPlaylistsClick = {
                navController.navigate(
                    AppDestination.LibraryTabs.createRoute(LibraryTab.PLAYLISTS)
                )
            }
        )
    }

    composable(AppDestination.Settings.route) {
        SettingsRoute(
            onProfileClick = {
                navController.navigate(
                    AppDestination.EditProfile.route
                )
            },
            onNotificationClick = { },
            onHelpClick = {
                navController.navigate(AppDestination.Help.route)
            },
            onAboutClick = {
                navController.navigate(AppDestination.About.route)
            },
            onEditProfileClick = {
                navController.navigate(AppDestination.EditProfile.route)
            },
            onFavoritesClick = {
                navController.navigate(
                    AppDestination.SongCollection.createRoute(
                        SongCollectionType.FAVORITES
                    )
                )
            },
            onHistoryClick = {
                navController.navigate(
                    AppDestination.SongCollection.createRoute(
                        SongCollectionType.HISTORY
                    )
                )
            }
        )
    }

    composable(AppDestination.EditProfile.route) {
        EditProfileRoute(
            onBackClick = { navController.popBackStack() },
            onSaveSuccess = { navController.popBackStack() }
        )
    }

    composable(AppDestination.Help.route) {
        HelpRoute(
            onBackClick = { navController.popBackStack() },
            onChatClick = {},
            onInfoClick = {}
        )
    }

    composable(AppDestination.About.route) {
        AboutRoute(
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(AppDestination.SongCollection.route) { backStack ->
        val typeName = backStack.arguments
            ?.getString("type")
            ?:return@composable

        val type = SongCollectionType.valueOf(typeName)

        SongCollectionRoute(
            type = type,
            onBackClick = { navController.popBackStack() },
            playerViewModel = playerViewModel,
        )
    }

    composable(AppDestination.NowPlaying.route) {
        NowPlayingRoute(
            onBack = { navController.popBackStack() },
            onArtistClick = { artistId ->
                navController.navigate(
                    AppDestination.ArtistDetail.createRoute(artistId)
                )
            },
            playerViewModel = playerViewModel,
        )
    }

    composable(AppDestination.Library.route) {
        LibraryRoute(
            navController = navController,
            playerViewModel = playerViewModel,
            onProfileClick = {
                navController.navigate(AppDestination.EditProfile.route)
            },
            initialTab = LibraryTab.PLAYLISTS
        )
    }

    composable(AppDestination.LibraryTabs.route) { backStackEntry ->
        val tabName = backStackEntry.arguments?.getString("tab")
        val tab = tabName?.let { LibraryTab.valueOf(it) } ?: LibraryTab.PLAYLISTS

        LibraryRoute(
            navController = navController,
            playerViewModel = playerViewModel,
            onProfileClick = {
                navController.navigate(AppDestination.EditProfile.route)
            },
            initialTab = tab
        )
    }

    composable(AppDestination.AddPlaylist.route) {
        AddPlaylistRoute(
            playlistId = null,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(AppDestination.EditPlaylist.route) { backStack ->

        val playlistId = backStack.arguments
            ?.getString("playlistId")
            ?.toLong()

        AddPlaylistRoute(
            playlistId = playlistId,
            onBackClick = { navController.popBackStack() },
            onAddSongClick = { playlistId ->
                navController.navigate(
                    AppDestination.AddSongToPlaylist.createRoute(playlistId)
                )
            }
        )
    }

    composable(AppDestination.AddSongToPlaylist.route) { backStackEntry ->
        val playlistId = backStackEntry.arguments
            ?.getString("playlistId")
            ?.toLong() ?: return@composable

        AddSongToPlaylistRoute(
            playlistId = playlistId,
            onBackClick = { navController.popBackStack() }
        )
    }


    composable(AppDestination.ArtistDetail.route) { backStack ->
        val artistId = backStack.arguments
            ?.getString("artistId")
            ?.toLong() ?: return@composable

        val authState by authViewModel.authState.collectAsState()

        val currentUserId = when (authState) {
            is AuthState.LoggedIn ->
                (authState as AuthState.LoggedIn).user.id
            else -> -1L
        }

        ArtistDetailRoute(
            artistId = artistId,
            currentUserId = currentUserId,
            playerViewModel = playerViewModel,
            onBackClick = { navController.popBackStack() },
            onAlbumClick = { albumId ->
                navController.navigate(
                    AppDestination.AlbumDetail.createRoute(albumId)
                )
            }
        )
    }

    composable(AppDestination.AlbumDetail.route) { backStack ->
        val albumId = backStack.arguments
            ?.getString("albumId")
            ?.toLong() ?: return@composable
        AlbumDetailRoute(
            albumId = albumId,
            playerViewModel = playerViewModel,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(AppDestination.PlaylistDetail.route) { backStack ->
        val playlistId = backStack.arguments
            ?.getString("playlistId")
            ?.toLong() ?: return@composable
        PlaylistDetailRoute(
            playlistId = playlistId,
            playerViewModel = playerViewModel,
            onBackClick = { navController.popBackStack() }
        )
    }

}

@Composable
fun MainNavGraph(
    navController: NavHostController,
    playerViewModel: PlayerViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route,
        modifier = modifier
    ) {
        mainNavGraph(
            navController = navController,
            playerViewModel = playerViewModel,
            authViewModel = authViewModel
        )
    }
}
