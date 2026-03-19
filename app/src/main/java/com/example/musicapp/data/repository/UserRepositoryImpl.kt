package com.example.musicapp.data.repository

import com.example.musicapp.data.db.dao.UserDao
import com.example.musicapp.data.db.entity.UserEntity
import com.example.musicapp.data.mapper.toDomain
import com.example.musicapp.data.mapper.toEntity
import com.example.musicapp.domain.model.User
import com.example.musicapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeCurrentUser(): Flow<User?> {
        return userDao.observeLoggedInUser().map { it?.toDomain() }
    }

    override suspend fun getUserById(id: Long): User? {
        return userDao.getUserById(id)?.toDomain()
    }

    override suspend fun setUserBanned(userId: Long, isBanned: Boolean) {
        userDao.setUserBanned(userId, isBanned)
    }

    override suspend fun saveUser(user: User) {
        userDao.updateUser(user.toEntity())
    }

    override suspend fun insertUser(user: User) {
        userDao.insertUser(
            UserEntity(
                username = user.username,
                email = user.email,
                password = user.password,
                avtUrl = user.avtUrl,
                bio = user.bio,
                role = user.role,
                isLoggedIn = user.isLoggedIn,
                createdAt = user.createdAt,
                isBanned = user.isBanned
            )
        )
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(
            UserEntity(
                id = user.id,
                username = user.username,
                email = user.email,
                password = user.password,
                avtUrl = user.avtUrl,
                bio = user.bio,
                role = user.role,
                isLoggedIn = user.isLoggedIn,
                createdAt = user.createdAt,
                isBanned = user.isBanned
            )
        )
    }

    override suspend fun deleteUser(id: Long) {
        userDao.deleteUser(id)
    }

    override suspend fun logout() {
        userDao.logoutAll()
    }
}