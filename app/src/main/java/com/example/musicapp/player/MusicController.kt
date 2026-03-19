package com.example.musicapp.player

import com.example.musicapp.domain.model.Song
import kotlinx.coroutines.flow.StateFlow

interface MusicController {

    val isPlaying: StateFlow<Boolean>
    val currentPositionMs: StateFlow<Long>
    val durationMs: StateFlow<Long>
    val currentSong: StateFlow<Song?>

    fun play(song: Song)
    fun togglePlayPause()
    fun seekTo(positionMs: Long)
    fun updateProgress()
    fun release()
    fun setOnSongCompletionListener(listener: () -> Unit)
}