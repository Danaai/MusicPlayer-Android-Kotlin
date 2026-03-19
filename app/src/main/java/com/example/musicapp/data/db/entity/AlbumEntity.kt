package com.example.musicapp.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "albums",
    indices = [Index("artistId")]
)
data class AlbumEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,

    val artistId: Long,
    val releaseDate: String? = null,

    val imageUrl: String? = null
)
