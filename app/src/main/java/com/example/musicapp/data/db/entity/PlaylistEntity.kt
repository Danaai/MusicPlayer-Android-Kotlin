package com.example.musicapp.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlists",
    indices = [Index("ownerUserId")]
)
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val description: String? = null,

    val ownerUserId: Long,

    val imageUrl: String? = null,

    val isPublic: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
