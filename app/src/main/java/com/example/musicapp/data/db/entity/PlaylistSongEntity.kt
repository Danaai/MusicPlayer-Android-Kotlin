package com.example.musicapp.data.db.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "playlist_songs",
    primaryKeys = ["playlistId", "songId"],
    indices = [Index("playlistId"), Index("songId")]
)
data class PlaylistSongEntity(
    val playlistId: Long,
    val songId: Long,

    val position: Int,
    val addedAt: Long = System.currentTimeMillis()
)
