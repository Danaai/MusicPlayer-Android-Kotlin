package com.example.musicapp.data.repository

import com.example.musicapp.data.db.dao.UserDao
import com.example.musicapp.data.db.entity.UserEntity
import com.example.musicapp.data.mapper.toDomain
import com.example.musicapp.domain.model.User
import com.example.musicapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(
        username: String,
        email: String,
        password: String
    ): Result<User> {

        val user = userDao.login(username, email ,password)
            ?: return Result.failure(Exception("Invalid credentials or user is banned"))

        userDao.logoutAll()
        userDao.setLoggedIn(user.id)

        return Result.success(user.toDomain())
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<User> {

        return try {

            userDao.logoutAll()

            val newUser = UserEntity(
                id = 0,
                username = username,
                email = email,
                password = password,
                role = "user",
                isLoggedIn = true
            )

            userDao.insertUser(newUser)

            Result.success(newUser.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        userDao.logoutAll()
    }

    override fun observeCurrentUser(): Flow<User?> {
        return userDao.observeLoggedInUser()
            .map { entity ->
                entity?.toDomain()
            }
    }
}