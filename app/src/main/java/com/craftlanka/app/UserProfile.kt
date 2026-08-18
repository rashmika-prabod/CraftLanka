package com.craftlanka.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.craftlanka.app.data.AuthRepository
import com.google.firebase.auth.FirebaseAuth

class UserProfile : AppCompatActivity() {

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        loadUserProfile()
        setupNotificationSwitches()
        setupMenuItems()
        setupBottomNavigation()

        findViewById<View>(R.id.btn_logout).setOnClickListener {
            logout()
        }

        findViewById<View>(R.id.delete_account_card).setOnClickListener {
            Toast.makeText(this, "Delete Account clicked", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Loads the currently logged-in buyer's real profile from Firestore
     * (via the same AuthRepository your friend built for login) and
     * displays it in the profile card.
     */
    private fun loadUserProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            // No one is logged in — shouldn't normally happen if this screen
            // is only reachable post-login, but guard against it anyway.
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        authRepository.getBuyerProfile(uid) { profile ->
            val nameView = findViewById<TextView>(R.id.tv_user_name)
            nameView.text = profile?.fullName?.ifBlank { "Buyer" } ?: "Buyer"
        }
    }

    /**
     * Signs the user out of Firebase Auth, clears locally saved
     * role/session info, and returns them to the login flow.
     */
    private fun logout() {
        FirebaseAuth.getInstance().signOut()

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit {
            remove("user_role")
            remove("logged_user")
            remove("user_name")
            remove("remember_me")
        }

        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupNotificationSwitches() {
        // Order Updates
        findViewById<View>(R.id.row_order_updates).apply {
            findViewById<TextView>(R.id.tv_label).text = "Order Updates"
            findViewById<SwitchCompat>(R.id.switch_action).isChecked = true
        }

        // Deals & Promotions
        findViewById<View>(R.id.row_deals).apply {
            findViewById<TextView>(R.id.tv_label).text = "Deals & Promotions"
            findViewById<SwitchCompat>(R.id.switch_action).isChecked = true
        }

        // Wishlist Alerts
        findViewById<View>(R.id.row_wishlist).apply {
            findViewById<TextView>(R.id.tv_label).text = "Wishlist Alerts"
            findViewById<SwitchCompat>(R.id.switch_action).isChecked = true
        }

        // Seller Messages
        findViewById<View>(R.id.row_messages).apply {
            findViewById<TextView>(R.id.tv_label).text = "Seller Messages"
            findViewById<SwitchCompat>(R.id.switch_action).isChecked = true
        }

        // Review Reminders
        findViewById<View>(R.id.row_reviews).apply {
            findViewById<TextView>(R.id.tv_label).text = "Review Reminders"
            findViewById<SwitchCompat>(R.id.switch_action).isChecked = false
        }
    }

    private fun setupMenuItems() {
        findViewById<View>(R.id.menu_privacy).apply {
            findViewById<TextView>(R.id.tv_menu_label).text = "Privacy & Security"
        }
        findViewById<View>(R.id.menu_language).apply {
            findViewById<TextView>(R.id.tv_menu_label).text = "Language & Region"
        }
        findViewById<View>(R.id.menu_appearance).apply {
            findViewById<TextView>(R.id.tv_menu_label).text = "Appearance"
        }
        findViewById<View>(R.id.menu_help).apply {
            findViewById<TextView>(R.id.tv_menu_label).text = "Help & Support"
        }
        findViewById<View>(R.id.menu_terms).apply {
            findViewById<TextView>(R.id.tv_menu_label).text = "Terms & Privacy"
        }
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.btnNavExplore).setOnClickListener {
            val intent = Intent(this, MainDisplay::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            finish()
        }
        findViewById<View>(R.id.btnNavNotification).setOnClickListener {
            startActivity(Intent(this, Notification::class.java))
            finish()
        }
        findViewById<View>(R.id.btnNavOrders).setOnClickListener {
            startActivity(Intent(this, Order::class.java))
            finish()
        }
        findViewById<View>(R.id.btnNavCart).setOnClickListener {
            startActivity(Intent(this, Cart::class.java))
            finish()
        }
        // Already on Profile - no action needed
    }
}
