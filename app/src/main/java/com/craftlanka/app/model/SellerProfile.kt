package com.craftlanka.app.model

data class SellerProfile(
    val uid: String = "",
    val ownerName: String = "",
    val phone: String = "",
    val businessName: String = "",
    val description: String = "",
    val addressNo: String = "",
    val road: String = "",
    val city: String = "",
    val country: String = "Sri Lanka",
    val email: String = "",
    val photoUrl: String = "",
    val isApproved: Boolean = false,
    val autoAcceptRequests: Boolean = false,
    val bankDetails: BankDetails? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
