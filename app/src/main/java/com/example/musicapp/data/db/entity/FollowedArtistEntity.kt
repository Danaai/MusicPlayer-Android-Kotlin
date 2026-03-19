package com.example.musicapp.data.db.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "followed_artists",
    primaryKeys = ["userId", "artistId"],
    indices = [Index("artistId")]
)
data class FollowedArtistEntity(
    val userId: Long,
    val artistId: Long,
    val followedAt: Long = System.currentTimeMillis()
)