package com.example.musicapp.ui.player

import android.content.Context

class PlayerStateStorage(private val context: Context) {

    private val prefs =
        context.getSharedPreferences("player_state", Context.MODE_PRIVATE)

    fun save(songId: Long, position: Long, isPlaying: Boolean) {

        prefs.edit()
            .putLong("songId", songId)
            .putLong("position", position)
            .putBoolean("isPlaying", isPlaying)
            .apply()
    }

    fun load(): PlayerState? {

        val songId = prefs.getLong("songId", -1)
        val position = prefs.getLong("position", 0)
        val isPlaying = prefs.getBoolean("isPlaying", false)

        if (songId == -1L) return null

        return PlayerState(songId, position, isPlaying)
    }
}

data class PlayerState(
    val songId: Long,
    val position: Long,
    val isPlaying: Boolean
)