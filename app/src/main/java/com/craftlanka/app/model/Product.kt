package com.craftlanka.app.model

data class Product(
    val productId: String = "",
    val sellerUid: String = "",
    val productName: String = "",
    val description: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val stockQuantity: Int = 0,
    val imageUrl: String = "",
    val viewCount: Int = 0,
    val soldCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis(),
)
