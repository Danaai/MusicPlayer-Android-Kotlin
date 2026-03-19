package com.example.musicapp.domain.model

data class User(
    val id: Long,

    val username: String,
    val email: String,
    val password: String,

    val avtUrl: String? = null,
    val bio: String? = null,

    val role: String,
    val isLoggedIn: Boolean = false,

    val createdAt: String? = null,
    val isBanned: Boolean = false
)