package com.craftlanka.app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.craftlanka.app.data.ProductRepository
import com.craftlanka.app.databinding.ActivityMainDisplayBinding

class MainDisplay : AppCompatActivity() {
    private lateinit var binding: ActivityMainDisplayBinding
    private lateinit var adapter: ProductAdapter
    private val productRepository = ProductRepository()
    private var allProducts: List<Product> = emptyList()

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
        loadProducts()
    }

    private fun loadProducts() {
        productRepository.fetchAllProducts(
            onSuccess = { products ->
                allProducts = products
                adapter.updateProducts(products)
            },
            onFailure = { error ->
                Toast.makeText(this, "Failed to load products: $error", Toast.LENGTH_SHORT).show()
            },
        )
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(emptyList()) { product ->
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
