package com.example.musicapp.domain.model

data class Song(
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