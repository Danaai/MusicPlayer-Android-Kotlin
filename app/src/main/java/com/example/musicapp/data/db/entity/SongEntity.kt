package com.example.musicapp.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "songs",
    indices = [Index("artistId"), Index("albumId")]
)
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,

    val artistId: Long,
    val albumId: Long? = null,

    val durationMs: Long,
    val genre: String? = null,

    val audioUrl: String,
    val imageUrl: String? = null,

    val releaseDate: String? = null
)
