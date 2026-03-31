package com.example.test_kotlin_app


data class User(var name: String = "", var email: String = "", var address: String = "", var description: String = ""){

    fun toHashMap(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["name"] = name
        map["email"] = email
        map["address"] = address
        map["description"] = description
        return map
    }
}