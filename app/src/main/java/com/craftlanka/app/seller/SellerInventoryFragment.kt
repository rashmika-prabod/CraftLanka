package com.craftlanka.app.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.craftlanka.app.MainActivity
import com.craftlanka.app.R
import com.craftlanka.app.data.AuthRepository
import com.craftlanka.app.data.SellerRepository
import com.craftlanka.app.databinding.FragmentSellerInventoryBinding
import com.craftlanka.app.model.Product
import com.google.firebase.auth.FirebaseAuth

class SellerInventoryFragment : Fragment() {

    private var bindingVar: FragmentSellerInventoryBinding? = null
    private val binding get() = bindingVar!!

    private lateinit var inventoryAdapter: SellerInventoryAdapter
    private val authRepository = AuthRepository()
    private val sellerRepository = SellerRepository()

    private val currentUid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentSellerInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupBottomNavigation()
        loadSellerProfile()
        fetchInventoryData()

        binding.btnProfileHeader.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(
                fragment = SellerProfileFragment(),
                addToBackStack = true,
            )
        }
    }

    private fun setupRecyclerView() {
        inventoryAdapter = SellerInventoryAdapter(
            products = emptyList(),
            onUpdateStockClick = { product ->
                (activity as? MainActivity)?.navigationManager?.replaceFragment(
                    fragment = UpdateStockFragment.newInstance(product.productId),
                    addToBackStack = true,
                )
            },
        )
        binding.rvInventoryActions.adapter = inventoryAdapter
    }

    private fun loadSellerProfile() {
        val uid = currentUid
        if (uid.isEmpty()) return
        authRepository.getSellerProfile(uid) { profile ->
            if (isAdded && profile != null && profile.photoUrl.isNotEmpty()) {
                Glide.with(this).load(profile.photoUrl).circleCrop().into(binding.ivHeaderProfile)
            }
        }
    }

    private fun fetchInventoryData() {
        val uid = currentUid
        if (uid.isEmpty()) return

        sellerRepository.getSellerProducts(
            uid,
            onSuccess = { products ->
                if (isAdded) {
                    calculateAndDisplayStats(products)
                }
            },
            onFailure = { error ->
                if (isAdded) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            },
        )
    }

    private fun calculateAndDisplayStats(products: List<Product>) {
        val inStock = products.filter { it.stockQuantity > 5 }.size
        val lowStock = products.filter { it.stockQuantity in 1..5 }.size
        val outOfStock = products.filter { it.stockQuantity == 0 }.size

        binding.tvInStockCount.text = inStock.toString()
        binding.tvLowStockCount.text = lowStock.toString()
        binding.tvOutOfStockCount.text = outOfStock.toString()

        // Items requiring action (low or out of stock)
        val actionRequired = products.filter { it.stockQuantity <= 5 }.sortedBy { it.stockQuantity }
        inventoryAdapter.updateData(actionRequired)
    }

    private fun setupBottomNavigation() {
        val nav = binding.includeBottomNav
        resetAllNavItems()
        updateNavItemVisuals(nav.ivNavInventory, nav.tvNavInventory, true)

        nav.navHome.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerHomeFragment(), false)
        }
        nav.navRequests.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerRequestsFragment(), false)
        }
        nav.navProducts.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerProductsFragment(), false)
        }
        nav.navAnalytics.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerAnalyticsFragment(), false)
        }
        nav.navProfile.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerProfileFragment(), false)
        }
    }

    private fun resetAllNavItems() {
        val nav = binding.includeBottomNav
        updateNavItemVisuals(nav.ivNavHome, nav.tvNavHome, false)
        updateNavItemVisuals(nav.ivNavRequests, nav.tvNavRequests, false)
        updateNavItemVisuals(nav.ivNavProducts, nav.tvNavProducts, false)
        updateNavItemVisuals(nav.ivNavAnalytics, nav.tvNavAnalytics, false)
        updateNavItemVisuals(nav.ivNavInventory, nav.tvNavInventory, false)
        updateNavItemVisuals(nav.ivNavProfile, nav.tvNavProfile, false)
    }

    private fun updateNavItemVisuals(icon: ImageView, label: TextView, isSelected: Boolean) {
        val context = requireContext()
        val color = if (isSelected) R.color.nav_active_content else R.color.nav_inactive_content
        icon.setColorFilter(ContextCompat.getColor(context, color))
        label.setTextColor(ContextCompat.getColor(context, color))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingVar = null
    }
}
