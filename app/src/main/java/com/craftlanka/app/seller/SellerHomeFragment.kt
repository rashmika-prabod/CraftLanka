package com.craftlanka.app.seller

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.craftlanka.app.MainActivity
import com.craftlanka.app.R
import com.craftlanka.app.data.AuthRepository
import com.craftlanka.app.databinding.FragmentSellerHomeBinding
import com.craftlanka.app.databinding.ItemLowStockBinding
import com.google.firebase.auth.FirebaseAuth

class SellerHomeFragment : Fragment() {

    private var bindingVar: FragmentSellerHomeBinding? = null
    private val binding get() = bindingVar!!

    private val authRepository = AuthRepository()
    private val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

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
        setupBottomNavigation()

        // Initial mock data for the UI demonstration as per Figma design
        updateDashboardStats(
            products = 42,
            sales = 128,
            revenue = "450,200",
            lowStockCount = 8,
        )

        setupLowStockList()
    }

    private fun loadSellerProfile() {
        if (currentUid.isEmpty()) return

        authRepository.getSellerProfile(currentUid) { profile ->
            if (isAdded && profile != null) {
                // 1. Handle Profile Image or Initial based on registration data
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

    private fun setupListeners() {
        // Header Profile Click -> Navigate to Profile UI
        binding.btnProfile.setOnClickListener {
            // TODO: Redirect to Profile UI (Navigation Logic to be added)
            Toast.makeText(requireContext(), "Opening Profile...", Toast.LENGTH_SHORT).show()
        }

        // FAB Add Product Click -> Navigate to Add Product UI
        binding.fabAddProduct.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.navigationManager.replaceFragment(
                fragment = AddProductFragment(),
                addToBackStack = true,
            )
        }

        // View Inventory Button
        binding.btnViewInventory.setOnClickListener {
            // TODO: Redirect to Inventory UI
            Toast.makeText(requireContext(), "Opening Inventory...", Toast.LENGTH_SHORT).show()
        }

        // Manage Plan Button
        binding.btnManagePlan.setOnClickListener {
            // TODO: Redirect to Subscription Management
            Toast.makeText(requireContext(), "Opening Plan Management...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        // Ensure 'Home' is highlighted in the bottom navigation bar
        binding.includeBottomNav.sellerBottomNav.selectedItemId = R.id.nav_home

        binding.includeBottomNav.sellerBottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_requests -> {
                    // TODO: Navigate to Requests Screen
                    false
                }
                R.id.nav_products -> {
                    // TODO: Navigate to Products Screen
                    false
                }
                R.id.nav_analytics -> {
                    // TODO: Navigate to Analytics Screen
                    false
                }
                R.id.nav_inventory -> {
                    // TODO: Navigate to Inventory Screen
                    false
                }
                R.id.nav_profile -> {
                    // TODO: Navigate to Profile Screen
                    false
                }
                else -> false
            }
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

    private fun setupLowStockList() {
        // Real logic will fetch these from Firestore, using mock data for UI demo
        val lowStockItems =
            listOf(
                Pair("Traditional Clay Water Jug", "2"),
                Pair("Hand-woven Cotton Throw", "5"),
            )

        binding.layoutLowStockItems.removeAllViews()

        for (item in lowStockItems) {
            val itemBinding =
                ItemLowStockBinding.inflate(layoutInflater, binding.layoutLowStockItems, false)
            itemBinding.tvItemName.text = item.first
            itemBinding.tvStockBadge.text = getString(R.string.format_low_stock, item.second)
            binding.layoutLowStockItems.addView(itemBinding.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingVar = null
    }
}
