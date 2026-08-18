package com.craftlanka.app.seller

import android.os.Bundle
import android.util.TypedValue
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
import com.craftlanka.app.databinding.FragmentSellerHomeBinding
import com.craftlanka.app.databinding.ItemLowStockBinding
import com.craftlanka.app.model.Product
import com.google.firebase.auth.FirebaseAuth

class SellerHomeFragment : Fragment() {
    private var bindingVar: FragmentSellerHomeBinding? = null
    private val binding get() = bindingVar!!

    private val authRepository = AuthRepository()
    private val sellerRepository = SellerRepository()

    // Dynamic UID fetch to ensure we always have the current authenticated user
    private val currentUid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentSellerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        loadSellerProfile()
        setupListeners()
        setupCustomBottomNavigation()
        fetchDashboardData()
    }

    private fun loadSellerProfile() {
        val uid = currentUid
        if (uid.isEmpty()) return

        authRepository.getSellerProfile(uid) { profile ->
            if (isAdded && profile != null) {
                if (profile.photoUrl.isNotEmpty()) {
                    binding.ivProfilePhoto.visibility = View.VISIBLE
                    binding.tvProfileInitial.visibility = View.GONE
                    Glide.with(this)
                        .load(profile.photoUrl)
                        .circleCrop()
                        .into(binding.ivProfilePhoto)
                } else {
                    binding.ivProfilePhoto.visibility = View.GONE
                    binding.tvProfileInitial.visibility = View.VISIBLE
                    val initial = profile.ownerName.take(1).uppercase()
                    binding.tvProfileInitial.text = initial
                }
            }
        }
    }

    private fun fetchDashboardData() {
        val uid = currentUid
        if (uid.isEmpty()) return

        sellerRepository.getSellerProducts(
            sellerUid = uid,
            onSuccess = { products ->
                if (isAdded) {
                    calculateAndDisplayStats(products)
                }
            },
            onFailure = { error ->
                if (isAdded) {
                    Toast.makeText(requireContext(), "Error loading stats: $error", Toast.LENGTH_SHORT).show()
                }
            },
        )
    }

    private fun calculateAndDisplayStats(products: List<Product>) {
        val totalProducts = products.size
        val lowStockProducts = products.filter { it.stockQuantity <= 5 }
        val lowStockCount = lowStockProducts.size

        // Placeholder for sales/revenue until Orders module is implemented
        updateDashboardStats(
            products = totalProducts,
            sales = 0,
            revenue = "0",
            lowStockCount = lowStockCount,
        )

        displayLowStockList(lowStockProducts)
    }

    private fun displayLowStockList(lowStockItems: List<Product>) {
        binding.layoutLowStockItems.removeAllViews()

        if (lowStockItems.isEmpty()) {
            val emptyText = TextView(requireContext()).apply {
                text = "All items well stocked"
                setTextColor(ContextCompat.getColor(context, R.color.text_grey))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setPadding(0, 20, 0, 0)
            }
            binding.layoutLowStockItems.addView(emptyText)
            return
        }

        for (product in lowStockItems) {
            val itemBinding =
                ItemLowStockBinding.inflate(layoutInflater, binding.layoutLowStockItems, false)
            itemBinding.tvItemName.text = product.productName
            itemBinding.tvStockBadge.text = getString(R.string.format_low_stock, product.stockQuantity.toString())
            binding.layoutLowStockItems.addView(itemBinding.root)
        }
    }

    private fun setupListeners() {
        binding.btnProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Profile feature coming soon", Toast.LENGTH_SHORT).show()
        }

        binding.fabAddProduct.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(
                fragment = AddProductFragment.newInstance(),
                addToBackStack = true,
            )
        }

        binding.btnViewInventory.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(
                fragment = SellerProductsFragment(),
                addToBackStack = true,
            )
        }
    }

    private fun setupCustomBottomNavigation() {
        val nav = binding.includeBottomNav
        resetAllNavItems()
        updateNavItemVisuals(nav.ivNavHome, nav.tvNavHome, true)

        nav.navHome.setOnClickListener { /* Already here */ }

        nav.navProducts.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(
                fragment = SellerProductsFragment(),
                addToBackStack = false,
            )
        }

        nav.navInventory.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(
                fragment = SellerProductsFragment(),
                addToBackStack = false,
            )
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
        if (isSelected) {
            icon.setColorFilter(ContextCompat.getColor(context, R.color.nav_active_content))
            label.setTextColor(ContextCompat.getColor(context, R.color.nav_active_content))
        } else {
            icon.setColorFilter(ContextCompat.getColor(context, R.color.nav_inactive_content))
            label.setTextColor(ContextCompat.getColor(context, R.color.nav_inactive_content))
        }
    }

    private fun updateDashboardStats(
        products: Int,
        sales: Int,
        revenue: String,
        lowStockCount: Int,
    ) {
        binding.tvTotalProducts.text = products.toString()
        binding.tvTotalSales.text = sales.toString()
        binding.tvTotalRevenue.text = getString(R.string.format_revenue, revenue)
        binding.tvLowStockCount.text = lowStockCount.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingVar = null
    }
}
