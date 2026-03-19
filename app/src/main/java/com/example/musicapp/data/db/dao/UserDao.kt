package com.example.musicapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicapp.data.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE role = 'user' ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("UPDATE users SET isBanned = :isBanned WHERE id = :userId")
    suspend fun setUserBanned(userId: Long, isBanned: Boolean)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: Long)

    @Query("SELECT * FROM users WHERE (username = :username OR email = :email) AND password = :password AND isBanned = 0 LIMIT 1")
    suspend fun login(username: String, email: String, password: String): UserEntity?

    @Query("UPDATE users SET isLoggedIn = 1 WHERE id = :userId")
    suspend fun setLoggedIn(userId: Long)

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun observeLoggedInUser(): Flow<UserEntity?>

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAll()

}