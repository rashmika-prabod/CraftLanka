package com.craftlanka.app.model

data class User(
    val uid: String = "",
    val email: String = "",
    val role: String = "", // "buyer", "seller", or "admin"
    val createdAt: Long = System.currentTimeMillis()
)