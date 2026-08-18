package com.craftlanka.app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.craftlanka.app.databinding.ActivityMainDisplayBinding

class MainDisplay : AppCompatActivity() {

    private lateinit var binding: ActivityMainDisplayBinding
    private lateinit var adapter: ProductAdapter

    private val allProducts = listOf(
        Product("Minimalist Ceramic Vase", "by Clara's Clay", "LKR 4,500", 4500.0, 4.9, 128, "Ceramics", true),
        Product("Frayed Linen Napkin Set", "by Loom & Thread", "LKR 4,500", 4500.0, 4.8, 85, "Textiles", true),
        Product("Cedar & Sage Soy Candle", "by Ember Studios", "LKR 3,200", 3200.0, 4.7, 210, "Ceramics", false),
        Product("Walnut Live-Edge Board", "by Timber Craft", "LKR 7,500", 7500.0, 4.9, 56, "Jewelry", true),
        Product("Handwoven Cotton Scarf", "by Loom & Thread", "LKR 2,800", 2800.0, 4.6, 42, "Textiles", true),
        Product("Gold Plated Earrings", "by Shine Gems", "LKR 8,200", 8200.0, 5.0, 15, "Jewelry", false),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupRecyclerView()
        setupSearch()
        setupBottomNav()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(allProducts) { product ->
            CartManager.addProduct(product)
        }
        binding.rvProducts.layoutManager = GridLayoutManager(this, 2)
        binding.rvProducts.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterProducts(query: String) {
        val filtered = allProducts.filter {
            it.title.contains(query, ignoreCase = true) || it.artisan.contains(query, ignoreCase = true)
        }
        adapter.updateProducts(filtered)
    }

    private fun setupBottomNav() {
        binding.btnNavCart.setOnClickListener {
            startActivity(Intent(this, Cart::class.java))
        }
        binding.btnNavOrders.setOnClickListener {
            startActivity(Intent(this, Order::class.java))
        }
        binding.btnNavNotification.setOnClickListener {
            startActivity(Intent(this, Notification::class.java))
        }
        binding.btnNavProfile.setOnClickListener {
            startActivity(Intent(this, UserProfile::class.java))
        }
    }
}
