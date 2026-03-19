package com.example.musicapp.domain.repository

import com.example.musicapp.data.db.entity.UserEntity
import com.example.musicapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(
        username: String,
        email: String,
        password: String
    ): Result<User>

    suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<User>

    suspend fun logout()

    fun observeCurrentUser(): Flow<User?>
}