package com.example.musicapp.data.db.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "recently_played",
    primaryKeys = ["userId", "songId"],
    indices = [Index("songId")]
)
data class RecentlyPlayedEntity(
    val userId: Long,
    val songId: Long,
    val playedAt: Long = System.currentTimeMillis()
)
