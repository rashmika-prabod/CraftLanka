package com.craftlanka.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.craftlanka.app.databinding.ActivityNotificationBinding

class Notification : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0) // Padding handled by bottom nav
            insets
        }

        setupRecyclerView()
        setupBottomNav()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        val notifications = listOf(
            NotificationData(0, NotificationType.HEADER, "", "", "", isHeader = true, headerTitle = "RECENT"),
            NotificationData(1, NotificationType.ORDER_UPDATE, "Order Update", "2m ago", "Your order #ORD-1842 from Moonlight Jewelry Co. has been shipped!"),
            NotificationData(2, NotificationType.MESSAGE, "Message", "45m ago", "Clara from Clara's Clay replied to your inquiry about the Minimalist Ceramic Vase.", extraText = "\"Hi! I can definitely customize the glaze color for you...\""),
            NotificationData(3, NotificationType.HEADER, "", "", "", isHeader = true, headerTitle = "EARLIER"),
            NotificationData(4, NotificationType.PROMOTION, "Promotion", "3h ago", "Flash Sale! 15% off all hand-woven textiles today only. Refresh your home for the season."),
            NotificationData(5, NotificationType.PRICE_DROP, "Price Drop", "Yesterday", "An item in your wishlist 'Ceylon Candle Set' is now 10% off."),
            NotificationData(6, NotificationType.FEEDBACK, "Feedback", "2 days ago", "How was your experience with Loom & Thread? Leave a review for your recent purchase."),
        )

        binding.rvNotifications.layoutManager = LinearLayoutManager(this)
        binding.rvNotifications.adapter = NotificationsAdapter(notifications)
    }

    private fun setupBottomNav() {
        // Notification is active, no need for click listener on it

        binding.btnNavExplore.setOnClickListener {
            val intent = Intent(this, MainDisplay::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            finish()
        }

        binding.btnNavOrders.setOnClickListener {
            startActivity(Intent(this, Order::class.java))
            finish()
        }

        binding.btnNavCart.setOnClickListener {
            startActivity(Intent(this, Cart::class.java))
            finish()
        }

        binding.btnNavProfile.setOnClickListener {
            // Profile logic
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
