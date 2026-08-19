package com.craftlanka.app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class CartActivity : AppCompatActivity() {

    private lateinit var tvCurrentAddress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        tvCurrentAddress = findViewById(R.id.tvCurrentAddress)

        // Basic Navigation
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Add / Update Address Navigation
        findViewById<TextView>(R.id.btnUpdateAddress).setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            startActivity(intent)
        }

        // Mock interaction for Promo Code
        findViewById<TextView>(R.id.btnPromoAdd).setOnClickListener {
            // Placeholder for promo logic
        }

        // Link to Checkout Activity
        findViewById<AppCompatButton>(R.id.btnProceedCheckout).setOnClickListener {
            val intent = Intent(this, Checkout::class.java)
            startActivity(intent)
        }

        updateAddressDisplay()
    }

    override fun onResume() {
        super.onResume()
        updateAddressDisplay()
    }

    private fun updateAddressDisplay() {
        AddressManager.currentAddress?.let {
            tvCurrentAddress.text = "${it.addressLine}, ${it.city}"
        } ?: run {
            tvCurrentAddress.text = "No address added yet"
        }
    }
}
