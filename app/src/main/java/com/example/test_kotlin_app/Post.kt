package com.example.test_kotlin_app

import kotlinx.serialization.Serializable

@Serializable
class Post(val date: String = "", val data: String = "") {
}