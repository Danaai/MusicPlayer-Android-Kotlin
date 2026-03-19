package com.example.musicapp.data.mapper

import com.example.musicapp.data.db.entity.UserEntity
import com.example.musicapp.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        username = username,
        email = email,
        password = password,
        avtUrl = avtUrl,
        bio = bio,
        role = role,
        isLoggedIn = isLoggedIn,
        createdAt = createdAt,
        isBanned = isBanned
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        username = username,
        email = email,
        password = password,
        avtUrl = avtUrl,
        bio = bio,
        role = role,
        isLoggedIn = isLoggedIn,
        createdAt = createdAt,
        isBanned = isBanned
    )
}