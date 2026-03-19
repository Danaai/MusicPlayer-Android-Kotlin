package com.example.musicapp.domain.model

data class Album(
    val id: Long,
    val title: String,
    val artistId: Long,
    val releaseDate: String? = null,
    val imageUrl: String? = null,
)
