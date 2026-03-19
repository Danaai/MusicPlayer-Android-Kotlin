package com.example.musicapp.data.db.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "favorite_songs",
    primaryKeys = ["userId", "songId"],
    indices = [Index("songId")]
)
data class FavoriteSongEntity(
    val userId: Long,
    val songId: Long,
    val addedAt: Long = System.currentTimeMillis()
)
