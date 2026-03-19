package com.example.musicapp.domain.model

data class Playlist(
    val id: Long,
    val name: String,
    val description: String?= null,

    val ownerId: Long,
    val ownerName: String,

    val imageUrl: String? = null,
    val songCount: Int = 0,

    val isPublic: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
)