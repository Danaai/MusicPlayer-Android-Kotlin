package com.example.musicapp.domain.model

data class Artist(
    val id: Long,
    val name: String,
    val imageUrl: String? = null,
    val info: String? = null,

    val popularity: Int = 0
)