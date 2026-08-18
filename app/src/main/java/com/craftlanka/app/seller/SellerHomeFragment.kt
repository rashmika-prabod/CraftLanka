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
        setupCustomBottomNavigation()

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
        binding.btnProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Opening Profile...", Toast.LENGTH_SHORT).show()
        }

        // NAVIGATION: Home -> Add Product
        binding.fabAddProduct.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.navigationManager.replaceFragment(
                fragment = AddProductFragment(),
                addToBackStack = true,
            )
        }

        binding.btnViewInventory.setOnClickListener {
            Toast.makeText(requireContext(), "Opening Inventory...", Toast.LENGTH_SHORT).show()
        }

        binding.btnManagePlan.setOnClickListener {
            Toast.makeText(requireContext(), "Opening Plan Management...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCustomBottomNavigation() {
        val nav = binding.includeBottomNav
        resetAllNavItems()
        updateNavItemVisuals(nav.ivNavHome, nav.tvNavHome, true)

        nav.navHome.setOnClickListener {
            resetAllNavItems()
            updateNavItemVisuals(nav.ivNavHome, nav.tvNavHome, true)
        }
        nav.navRequests.setOnClickListener {
            resetAllNavItems()
            updateNavItemVisuals(nav.ivNavRequests, nav.tvNavRequests, true)
        }
        nav.navProducts.setOnClickListener {
            resetAllNavItems()
            updateNavItemVisuals(nav.ivNavProducts, nav.tvNavProducts, true)
        }
        nav.navAnalytics.setOnClickListener {
            resetAllNavItems()
            updateNavItemVisuals(nav.ivNavAnalytics, nav.tvNavAnalytics, true)
        }
        nav.navInventory.setOnClickListener {
            resetAllNavItems()
            updateNavItemVisuals(nav.ivNavInventory, nav.tvNavInventory, true)
        }
        nav.navProfile.setOnClickListener {
            resetAllNavItems()
            updateNavItemVisuals(nav.ivNavProfile, nav.tvNavProfile, true)
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
            icon.setImageState(intArrayOf(android.R.attr.state_checked), true)
            label.setTextColor(ContextCompat.getColor(context, R.color.nav_active_content))
        } else {
            icon.setImageState(intArrayOf(), true)
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

    private fun setupLowStockList() {
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
