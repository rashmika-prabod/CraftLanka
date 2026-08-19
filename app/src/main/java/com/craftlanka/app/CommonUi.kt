package com.craftlanka.app

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Theme Colors
val BackgroundCream = Color(0xFFFDFCE7)
val MintGreen = Color(0xFFD1FADF)
val StarYellow = Color(0xFFFFB400)

data class Product(
    val title: String,
    val artisan: String,
    val priceString: String,
    val priceAmount: Double,
    val rating: Double,
    val reviews: Int,
    val category: String,
    val inStock: Boolean = true,
    val imageUrl: String = "",
)

data class UserAddress(
    val fullName: String,
    val phone: String,
    val addressLine: String,
    val city: String,
)

object AddressManager {
    var currentAddress by mutableStateOf<UserAddress?>(null)
}

data class PaymentCard(
    val holderName: String,
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String,
)

object PaymentManager {
    var currentCard by mutableStateOf<PaymentCard?>(null)
}

object CartManager {
    private val _items = mutableStateListOf<Product>()
    val items: List<Product> get() = _items

    fun addProduct(product: Product) {
        _items.add(product)
    }

    fun removeProduct(product: Product) {
        _items.remove(product)
    }

    fun clearCart() {
        _items.clear()
    }
}

data class NavItem(val label: String, val icon: ImageVector)

@Composable
fun CraftLankaBottomNav(currentTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.height(72.dp),
    ) {
        val navItems = listOf(
            NavItem("Explore", Icons.Default.Search),
            NavItem("Notification", Icons.Default.Notifications),
            NavItem("Orders", Icons.AutoMirrored.Filled.List),
            NavItem("Cart", Icons.Default.ShoppingCart),
            NavItem("Profile", Icons.Default.Person),
        )

        navItems.forEach { item ->
            val isActive = currentTab == item.label
            NavigationBarItem(
                selected = isActive,
                onClick = { onTabSelected(item.label) },
                icon = {
                    if (isActive) {
                        Surface(
                            color = MintGreen,
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                tint = Color.Black,
                            )
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.label, tint = Color.Gray)
                    }
                },
                label = {
                    Text(
                        item.label,
                        fontSize = 10.sp,
                        color = if (isActive) Color.Black else Color.Gray,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                ),
            )
        }
    }
}
