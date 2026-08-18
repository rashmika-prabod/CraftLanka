package com.craftlanka.app.model

data class BuyerRequest(
    val requestId: String = "",
    val buyerUid: String = "",
    val sellerUid: String = "",
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val location: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // PENDING, ACCEPTED, REJECTED
    val rejectionReason: String = "",
    val imageUrl: String = ""
)
