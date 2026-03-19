package com.example.musicapp.ui.components

import androidx.compose.runtime.Composable
import com.example.musicapp.ui.player.PlayerUiState

@Composable
fun MiniPlayer(
    uiState: PlayerUiState,
    onClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeek: (Float) -> Unit
) {

    val song = uiState.currentSong ?: return

    SongItem(
        song = song,
        artistName = uiState.currentArtistName,
        songType = SongItemType.MINI_PLAYER,
        isPlaying = uiState.isPlaying,
        progress = uiState.progress,
        onClick = onClick,
        onPlayPauseClick = onPlayPauseClick,
        onNextClick = onNextClick,
        onPreviousClick = onPreviousClick,
        onSeek = onSeek
    )
}