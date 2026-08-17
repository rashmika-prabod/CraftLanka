package com.craftlanka.app.model

data class User(
    val uid: String = "",
    val email: String = "",
    // "buyer", "seller", or "admin"
    val role: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)
