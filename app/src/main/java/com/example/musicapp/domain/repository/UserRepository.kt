package com.example.musicapp.domain.repository

import com.example.musicapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getAllUsers(): Flow<List<User>>

    fun observeCurrentUser(): Flow<User?>

    suspend fun getUserById(id: Long): User?

    suspend fun setUserBanned(userId: Long, isBanned: Boolean)

    suspend fun saveUser(user: User)

    suspend fun insertUser(user: User)

    suspend fun updateUser(user: User)

    suspend fun deleteUser(id: Long)

    suspend fun logout()
}