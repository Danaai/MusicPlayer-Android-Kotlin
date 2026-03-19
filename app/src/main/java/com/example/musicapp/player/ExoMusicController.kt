package com.example.musicapp.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicapp.domain.model.Song
import com.example.musicapp.ui.player.PlayerStateStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExoMusicController private constructor(
    context: Context
) : MusicController {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("player_prefs", Context.MODE_PRIVATE)
    private val storage = PlayerStateStorage(appContext)

    private val exoPlayer = ExoPlayer.Builder(context).build()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    override val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    override val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    override val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private var onSongComplete: (() -> Unit)? = null

    companion object {
        @Volatile
        private var INSTANCE: ExoMusicController? = null

        fun getInstance(context: Context): ExoMusicController {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ExoMusicController(context).also {
                    INSTANCE = it
                }
            }
        }
    }

    init {

        exoPlayer.addListener(object : Player.Listener {

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                saveState()
            }

            override fun onPlaybackStateChanged(state: Int) {
                if(state == Player.STATE_READY) {
                    _durationMs.value = exoPlayer.duration
                }

                if(state == Player.STATE_ENDED) {
                    onSongComplete?.invoke()
                }
            }
        })

        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(500)
                updateProgress()
            }
        }
    }

    override fun play(song: Song) {
        if(_currentSong.value?.id == song.id) {
            exoPlayer.play()
            return
        }

        _currentSong.value = song

        exoPlayer.setMediaItem(MediaItem.fromUri(song.audioUrl))
        exoPlayer.prepare()
        exoPlayer.play()

        saveState()
    }

    override fun togglePlayPause() {
        if (exoPlayer.isPlaying) exoPlayer.pause()
        else exoPlayer.play()
    }

    override fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        _currentPositionMs.value = positionMs
        saveState()
    }

    override fun updateProgress() {
        _currentPositionMs.value = exoPlayer.currentPosition
    }

    override fun release() {
        saveState()
        exoPlayer.release()
    }

    private fun saveState() {

        val song = _currentSong.value ?: return

        storage.save(
            songId = song.id,
            position = exoPlayer.currentPosition,
            isPlaying = exoPlayer.isPlaying
        )
    }

    fun restoreState(song: Song, position: Long, shouldPlay: Boolean) {
        play(song)

        exoPlayer.seekTo(position)

        if(!shouldPlay) {
            exoPlayer.pause()
        }
    }

    override fun setOnSongCompletionListener(listener: () -> Unit) {
        onSongComplete = listener
    }

}