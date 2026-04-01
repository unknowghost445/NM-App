package com.example.test_kotlin_app

import kotlinx.serialization.Serializable

@Serializable
data class User(var userID: String = "", var name: String = "", var email: String = "", var address: String = "", var avatarUrl: String = "", var description: String = ""){

}