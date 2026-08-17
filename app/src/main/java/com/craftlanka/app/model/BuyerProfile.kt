package com.craftlanka.app.model

data class BuyerProfile(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)
