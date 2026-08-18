package com.craftlanka.app.seller

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.craftlanka.app.databinding.FragmentSellerProductsBinding
import com.craftlanka.app.databinding.LayoutDeleteConfirmationModalBinding
import com.craftlanka.app.model.Product
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class SellerProductsFragment : Fragment() {

    private var bindingVar: FragmentSellerProductsBinding? = null
    private val binding get() = bindingVar!!

    private lateinit var productAdapter: SellerProductsAdapter
    private val authRepository = AuthRepository()
    private val sellerRepository = SellerRepository()

    private val currentUid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private var allProducts = listOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentSellerProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupBottomNavigation()
        loadSellerProfile()
        fetchSellerProducts()
        setupFilters()
        setupSearch()

        binding.btnProfileHeader.setOnClickListener {
            Toast.makeText(requireContext(), "Profile feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        productAdapter = SellerProductsAdapter(
            products = emptyList(),
            onUpdateClick = { product ->
                (activity as? MainActivity)?.navigationManager?.replaceFragment(
                    fragment = AddProductFragment.newInstance(product.productId),
                    addToBackStack = true,
                )
            },
            onDeleteClick = { product ->
                showDeleteConfirmation(product)
            },
        )
        binding.rvSellerProducts.adapter = productAdapter
    }

    private fun showDeleteConfirmation(product: Product) {
        val dialogBinding = LayoutDeleteConfirmationModalBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.Theme_CraftLanka_Dialog_Transparent)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialogBinding.tvDeleteDescription.text = "This will permanently remove '${product.productName}' from your inventory. This action cannot be undone."

        dialogBinding.btnCancelDelete.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnConfirmDelete.setOnClickListener {
            dialog.dismiss()
            performDelete(product)
        }

        dialog.show()
    }

    private fun performDelete(product: Product) {
        sellerRepository.deleteProduct(
            product.productId,
            onSuccess = {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Product deleted successfully", Toast.LENGTH_SHORT).show()
                    fetchSellerProducts()
                }
            },
            onFailure = { error ->
                if (isAdded) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            },
        )
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

    private fun fetchSellerProducts() {
        val uid = currentUid
        if (uid.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            return
        }

        sellerRepository.getSellerProducts(
            uid,
            onSuccess = { products ->
                if (isAdded) {
                    allProducts = products
                    filterProducts()
                }
            },
            onFailure = { error ->
                if (isAdded) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            },
        )
    }

    private fun setupFilters() {
        binding.chipGroupCategories.setOnCheckedStateChangeListener { _, _ ->
            filterProducts()
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterProducts() {
        val query = binding.etSearch.text.toString().lowercase()
        val checkedChipId = binding.chipGroupCategories.checkedChipId

        val filtered = allProducts.filter { product ->
            val matchesQuery = product.productName.lowercase().contains(query)
            val matchesCategory = when (checkedChipId) {
                R.id.chip_woodwork -> product.category.equals("Woodwork", ignoreCase = true)
                R.id.chip_pottery -> product.category.equals("Pottery", ignoreCase = true)
                else -> true
            }
            matchesQuery && matchesCategory
        }

        productAdapter.updateData(filtered)
        binding.layoutEmptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvSellerProducts.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun setupBottomNavigation() {
        val nav = binding.includeBottomNav
        resetAllNavItems()
        updateNavItemVisuals(nav.ivNavProducts, nav.tvNavProducts, true)

        nav.navHome.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerHomeFragment(), false)
        }
        nav.navProducts.setOnClickListener { fetchSellerProducts() }
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
