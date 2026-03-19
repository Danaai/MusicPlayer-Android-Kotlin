package com.example.musicapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.ui.admin.AdminDashBoardRoute
import com.example.musicapp.ui.admin.album_manangement.AddAlbumRoute
import com.example.musicapp.ui.admin.album_manangement.AddSongToAlbumRoute
import com.example.musicapp.ui.admin.album_manangement.AlbumManagementRoute
import com.example.musicapp.ui.admin.artist_management.AddArtistRoute
import com.example.musicapp.ui.admin.artist_management.ArtistManagementRoute
import com.example.musicapp.ui.admin.song_management.AddSongRoute
import com.example.musicapp.ui.admin.song_management.SongManagementRoute
import com.example.musicapp.ui.admin.user_management.AddUserRoute
import com.example.musicapp.ui.admin.user_management.UserManagementRoute
import com.example.musicapp.ui.auth.AuthState
import com.example.musicapp.ui.components.MainScaffold
import com.example.musicapp.ui.components.SplashScreen

@Composable
fun RootNavGraph(
    navController: NavHostController,
    authState: AuthState,
    factory: AppViewModelFactory
) {

    NavHost(
        navController = navController,
        startDestination = AppDestination.Splash.route
    ) {
        composable(AppDestination.Splash.route) {
            SplashScreen()
        }

        authNavGraph(navController)

        composable(AppDestination.Main.route) {
            MainScaffold(
                factory = factory
            )
        }

        composable(AppDestination.Admin.route) {
            AdminDashBoardRoute(
                onManageUserClick = {
                    navController.navigate(AppDestination.UserManagement.route)
                },
                onManageSongsClick = {
                    navController.navigate(AppDestination.SongManagement.route)
                },
                onManageArtistsClick = {
                    navController.navigate(AppDestination.ArtistManagement.route)
                },
                onManageAlbumClick = {
                    navController.navigate(AppDestination.AlbumManagement.route)
                }
            )
        }

        composable(AppDestination.SongManagement.route) {
            SongManagementRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onAddSong = {
                    navController.navigate(AppDestination.AddSong.route)
                },
                onEditSong = { songId ->
                    navController.navigate(
                        AppDestination.EditSong.createRoute(songId)
                    )
                }
            )
        }

        composable(AppDestination.AddSong.route) {
            AddSongRoute(
                songId = null,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppDestination.EditSong.route) { backStackEntry ->
            val songId =
                backStackEntry.arguments
                    ?.getString("songId")
                    ?.toLongOrNull()

            AddSongRoute(
                songId = songId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppDestination.ArtistManagement.route) {
            ArtistManagementRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onAddArtist = {
                    navController.navigate(AppDestination.AddArtist.route)
                },
                onEditArtist = { artistId ->
                    navController.navigate(
                        AppDestination.EditArtist.createRoute(artistId)
                    )
                }
            )
        }

        composable(AppDestination.AddArtist.route) {
            AddArtistRoute(
                artistId = null,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = AppDestination.EditArtist.route) { backStackEntry ->
            val artistId =
                backStackEntry.arguments
                    ?.getString("artistId")
                    ?.toLongOrNull()

            AddArtistRoute(
                artistId = artistId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppDestination.AlbumManagement.route) {
            AlbumManagementRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onAddAlbum = {
                    navController.navigate(AppDestination.AddAlbum.route)
                },
                onEditAlbum = { albumId ->
                    navController.navigate(
                        AppDestination.EditAlbum.createRoute(albumId)
                    )
                }
            )
        }

        composable(AppDestination.AddAlbum.route) {
            AddAlbumRoute(
                albumId = null,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = AppDestination.EditAlbum.route) { backStackEntry ->
            val albumId =
                backStackEntry.arguments
                    ?.getString("albumId")
                    ?.toLongOrNull()

            AddAlbumRoute(
                albumId = albumId,
                onBackClick = { navController.popBackStack() },
                onAddSongClick = { albumId ->
                    navController.navigate(
                        AppDestination.AddSongToAlbum.createRoute(albumId)
                    )
                }
            )
        }

        composable(route = AppDestination.AddSongToAlbum.route) { backStackEntry ->
            val albumId =
                backStackEntry.arguments
                    ?.getString("albumId")
                    ?.toLongOrNull() ?: return@composable

            AddSongToAlbumRoute(
                albumId = albumId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(AppDestination.UserManagement.route) {
            UserManagementRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onAddUser = {
                    navController.navigate(AppDestination.AddUser.route)
                },
                onEditUser = { userId ->
                    navController.navigate(
                        AppDestination.EditUser.createRoute(userId)
                    )
                }
            )
        }

        composable(AppDestination.AddUser.route) {
            AddUserRoute(
                userId = null,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = AppDestination.EditUser.route) { backStackEntry ->
            val userId =
                backStackEntry.arguments
                    ?.getString("userId")
                    ?.toLongOrNull()

            AddUserRoute(
                userId = userId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.LoggedIn -> {
                val destination =
                    if(authState.user.role == "admin") {
                        AppDestination.Admin.route
                    } else {
                        AppDestination.Main.route
                    }

                navController.navigate(destination) {
                    popUpTo(AppDestination.Splash.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            AuthState.LoggedOut -> {
                navController.navigate(AppDestination.Login.route) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            }

            else -> {}
        }
    }
}
