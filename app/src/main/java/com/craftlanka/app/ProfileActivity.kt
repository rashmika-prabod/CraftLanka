package com.craftlanka.app

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.header_background)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        setupNotificationToggles()
        setupMenuActions()
        setupBottomNavigation()
        setupProfileData()
    }

    private fun setupProfileData() {
        // Mocking profile data loading
        findViewById<TextView>(R.id.tv_name).text = "Amara Silva"
        findViewById<TextView>(R.id.tv_location).text = "Colombo, Sri Lanka"

        findViewById<TextView>(R.id.btn_edit_profile).setOnClickListener {
            Toast.makeText(this, "Edit Profile Clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.tv_view_all_orders).setOnClickListener {
            Toast.makeText(this, "Viewing all 28 orders", Toast.LENGTH_SHORT).show()
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btn_delete_account).setOnClickListener {
            Toast.makeText(this, "Delete Account functionality initiated", Toast.LENGTH_SHORT).show()
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_logout_main).setOnClickListener {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupNotificationToggles() {
        configureSwitchRow(R.id.row_order_updates, "Order Updates", true)
        configureSwitchRow(R.id.row_deals_promos, "Deals & Promotions", true)
        configureSwitchRow(R.id.row_wishlist_alerts, "Wishlist Alerts", true)
        configureSwitchRow(R.id.row_seller_messages, "Seller Messages", true)
        configureSwitchRow(R.id.row_review_reminders, "Review Reminders", false)
    }

    private fun configureSwitchRow(rowId: Int, label: String, isChecked: Boolean) {
        val row = findViewById<android.view.View>(rowId)
        row.findViewById<TextView>(R.id.tv_label).text = label
        val switch = row.findViewById<SwitchCompat>(R.id.switch_action)
        switch.isChecked = isChecked
        switch.setOnCheckedChangeListener { _, checked ->
            Toast.makeText(this, "$label: ${if (checked) "ON" else "OFF"}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupMenuActions() {
        configureMenuRow(R.id.menu_privacy_security, "Privacy & Security", android.R.drawable.ic_lock_lock)
        configureMenuRow(R.id.menu_language_region, "Language & Region", android.R.drawable.ic_menu_myplaces)
        configureMenuRow(R.id.menu_appearance, "Appearance", android.R.drawable.ic_menu_gallery)
        configureMenuRow(R.id.menu_help_support, "Help & Support", android.R.drawable.ic_menu_help)
        configureMenuRow(R.id.menu_terms_privacy, "Terms & Privacy", android.R.drawable.ic_menu_info_details)
    }

    private fun configureMenuRow(rowId: Int, label: String, iconRes: Int) {
        val row = findViewById<android.view.View>(rowId)
        row.findViewById<TextView>(R.id.tv_menu_label).text = label
        row.findViewById<android.widget.ImageView>(R.id.iv_menu_icon).setImageResource(iconRes)
        row.setOnClickListener {
            Toast.makeText(this, "Opening $label", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_profile)
        bottomNav.selectedItemId = R.id.nav_profile
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_explore -> {
                    Toast.makeText(this, "Explore", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_notifications -> {
                    Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_orders -> {
                    Toast.makeText(this, "Orders", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_cart -> {
                    Toast.makeText(this, "Cart", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }
}
