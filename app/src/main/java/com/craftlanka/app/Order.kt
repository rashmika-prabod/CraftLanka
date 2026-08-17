package com.craftlanka.app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.craftlanka.app.databinding.ActivityOrderBinding

class Order : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private lateinit var adapter: OrdersAdapter
    private var allOrders = listOf<OrderData>()
    private var currentStatus = OrderStatus.ALL
    private var currentQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupData()
        setupRecyclerView()
        setupSearch()
        setupFilters()
        setupBottomNav()
    }

    private fun setupData() {
        allOrders = listOf(
            OrderData("#ORD-0042", "Oct 12, 10:30 AM", "Benjamin Carter", "Ceramic Pasta Bowl", "x2 items", "$84.00", OrderStatus.PENDING),
            OrderData("#ORD-0041", "Oct 11, 4:15 PM", "Sophia Miller", "Organic Cotton Throw", "x1 item", "$120.00", OrderStatus.PROCESSING),
            OrderData("#ORD-0040", "Oct 11, 9:00 AM", "Marcus Thorne", "Walnut Desk Organizer", "x1 item", "$65.00", OrderStatus.SHIPPED),
            OrderData("#ORD-0039", "Oct 10, 2:45 PM", "Isabella Chen", "Hand-stitched Journal", "x3 items", "$135.00", OrderStatus.DELIVERED),
            OrderData("#ORD-0038", "Oct 09, 11:20 AM", "David Brooks", "Soy Wax Candle Set", "x1 item", "$45.00", OrderStatus.CANCELLED),
            OrderData("#ORD-0037", "Oct 06, 5:50 PM", "Elena Rodriguez", "Brass Pendant Necklace", "x1 item", "$110.00", OrderStatus.DELIVERED),
        )
    }

    private fun setupRecyclerView() {
        adapter = OrdersAdapter(allOrders) { order ->
            val intent = Intent(this, OrderDetails::class.java)
            startActivity(intent)
        }
        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        binding.rvOrders.adapter = adapter
    }

    private fun setupSearch() {
        binding.ivSearchIcon.setOnClickListener {
            if (binding.cvSearch.visibility == View.VISIBLE) {
                binding.cvSearch.visibility = View.GONE
                binding.etSearchOrders.text.clear()
            } else {
                binding.cvSearch.visibility = View.VISIBLE
                binding.etSearchOrders.requestFocus()
            }
        }

        binding.etSearchOrders.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s.toString()
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFilters() {
        val tabMap = mapOf(
            OrderStatus.ALL to binding.tabAll,
            OrderStatus.PENDING to binding.tabPending,
            OrderStatus.PROCESSING to binding.tabProcessing,
            OrderStatus.SHIPPED to binding.tabShipped,
            OrderStatus.DELIVERED to binding.tabDelivered,
            OrderStatus.CANCELLED to binding.tabCancelled,
        )

        tabMap.forEach { (status, textView) ->
            textView.setOnClickListener {
                currentStatus = status
                updateTabUI(tabMap)
                applyFilters()
            }
        }
    }

    private fun updateTabUI(tabs: Map<OrderStatus, TextView>) {
        tabs.forEach { (status, textView) ->
            val isActive = (status == currentStatus)
            textView.setBackgroundResource(if (isActive) R.drawable.bg_pill_active else R.drawable.bg_pill_inactive)
            textView.setTextColor(ContextCompat.getColor(this, if (isActive) R.color.white else R.color.charcoal))
        }
    }

    private fun applyFilters() {
        var filtered = allOrders

        if (currentStatus != OrderStatus.ALL) {
            filtered = filtered.filter { it.status == currentStatus }
        }

        if (currentQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.id.contains(currentQuery, ignoreCase = true) ||
                    it.productName.contains(currentQuery, ignoreCase = true) ||
                    it.customerName.contains(currentQuery, ignoreCase = true)
            }
        }

        adapter.updateOrders(filtered)
    }

    private fun setupBottomNav() {
        binding.btnNavExplore.setOnClickListener {
            val intent = Intent(this, MainDisplay::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            finish()
        }
        binding.btnNavCart.setOnClickListener {
            startActivity(Intent(this, Cart::class.java))
            finish()
        }
        binding.btnNavNotification.setOnClickListener {
            startActivity(Intent(this, Notification::class.java))
            finish()
        }
    }
}
