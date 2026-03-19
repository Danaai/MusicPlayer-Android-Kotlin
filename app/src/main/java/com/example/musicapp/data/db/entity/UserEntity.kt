package com.example.musicapp.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val username: String,
    val email: String,
    val password: String,

    val avtUrl: String? = null,
    val bio: String? = null,

    val role: String = "user",
    val isLoggedIn: Boolean = false,

    val createdAt: String? = null,
    val isBanned: Boolean = false
)