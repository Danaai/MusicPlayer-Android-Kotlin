package com.example.musicapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val imageUrl: String? = null,
    val info: String? = null,
    val popularity: Int = 0
)