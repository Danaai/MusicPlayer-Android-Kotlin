package com.example.musicapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.navigation.AppDestination
import com.example.musicapp.navigation.MainNavGraph
import com.example.musicapp.ui.auth.AuthViewModel
import com.example.musicapp.ui.player.NowPlayingRoute
import com.example.musicapp.ui.player.PlayerViewModel

@Composable
fun MainScaffold(
    factory: AppViewModelFactory
) {
    val navController = rememberNavController()

    val playerViewModel: PlayerViewModel = viewModel(factory = factory)
    val authViewModel: AuthViewModel = viewModel(factory =factory)

    val playerUiState by playerViewModel.uiState.collectAsState()

    var showNowPlaying by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Column {
                AnimatedVisibility(
                    visible = !showNowPlaying && playerUiState.currentSong != null,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ) {
                    MiniPlayer(
                        uiState = playerUiState,
                        onClick = { showNowPlaying = true },
                        onPlayPauseClick = playerViewModel::togglePlayPause,
                        onNextClick = playerViewModel::playNext,
                        onPreviousClick = playerViewModel::playPrevious,
                        onSeek = playerViewModel::seekTo
                    )
                }

                AppBottomBar(navController)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MainNavGraph(
                navController = navController,
                playerViewModel = playerViewModel,
                authViewModel = authViewModel
            )

            AnimatedVisibility(
                visible = showNowPlaying,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                NowPlayingRoute(
                    onBack = { showNowPlaying = false },
                    onArtistClick = { artistId ->
                        showNowPlaying = false
                        navController.navigate(AppDestination.ArtistDetail.createRoute(artistId))
                    },
                    playerViewModel = playerViewModel,
                )
            }
        }
    }
}