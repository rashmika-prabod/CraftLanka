package com.craftlanka.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.craftlanka.app.ui.theme.CraftLankaTheme

class Cart : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CraftLankaTheme {
                CartScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen() {
    val context = LocalContext.current
    val currentTab = "Cart"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Your Cart", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundCream,
                ),
            )
        },
        bottomBar = {
            CraftLankaBottomNav(
                currentTab = currentTab,
                onTabSelected = { tabName ->
                    when (tabName) {
                        "Explore" -> {
                            val intent = Intent(context, MainDisplay::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            context.startActivity(intent)
                        }
                        "Notification" -> {
                            val intent = Intent(context, Notification::class.java)
                            context.startActivity(intent)
                        }
                        "Orders" -> {
                            val intent = Intent(context, Order::class.java)
                            context.startActivity(intent)
                        }
                        "Profile" -> {
                            // Handle Profile if activity exists
                        }
                    }
                },
            )
        },
        containerColor = BackgroundCream,
    ) { innerPadding ->
        val cartItems = CartManager.items

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Your cart is empty",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Gray,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val intent = Intent(context, MainDisplay::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    ) {
                        Text("Start Shopping")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                // Cart Items
                cartItems.forEach { product ->
                    CartItem(product)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Order Summary
                val subtotal = cartItems.sumOf { it.priceAmount }
                val tax = subtotal * 0.08 // Example 8% tax
                val total = subtotal + tax

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        SummaryRow("Subtotal", "LKR ${String.format("%,.0f", subtotal)}")
                        SummaryRow("Estimated Shipping", "FREE", textColor = Color(0xFF0F5229))
                        SummaryRow("Tax", "LKR ${String.format("%,.0f", tax)}")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("LKR ${String.format("%,.0f", total)}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checkout Button
                Button(
                    onClick = {
                        val intent = Intent(context, Checkout::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Proceed to Checkout →", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CartItem(product: Product) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = true, onCheckedChange = {})
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color.LightGray.copy(0.2f), RoundedCornerShape(8.dp)),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(product.title, fontWeight = FontWeight.Bold)
            Text(product.priceString, color = Color.Gray)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { CartManager.removeProduct(product) }) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, textColor: Color = Color.Black) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Bold, color = textColor)
    }
}

@Preview(showBackground = true)
@Composable
fun CartPreview() {
    CraftLankaTheme {
        CartScreen()
    }
}
