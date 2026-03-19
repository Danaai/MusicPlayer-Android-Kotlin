package com.example.musicapp.data.datasource

import com.example.musicapp.domain.model.User

object LocalUserDataSource {

    private var user: User? = null

    fun saveUser(newUser: User) {
        user = newUser
    }

    fun getUser(): User? {
        return user
    }

    fun clearUser() {
        user = null
    }

}