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
import com.craftlanka.app.databinding.FragmentSellerProfileBinding
import com.google.firebase.auth.FirebaseAuth

class SellerProfileFragment : Fragment() {

    private var bindingVar: FragmentSellerProfileBinding? = null
    private val binding get() = bindingVar!!

    private val authRepository = AuthRepository()
    private val currentUid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentSellerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomNavigation()
        setupMenuRows()
        loadSellerProfile()
        setupListeners()
    }

    private fun setupMenuRows() {
        binding.rowEditShop.apply {
            ivMenuIcon.setImageResource(R.drawable.ic_store)
            tvMenuLabel.text = "Edit Shop Details"
        }

        binding.rowManageAccount.apply {
            ivMenuIcon.setImageResource(R.drawable.ic_person)
            tvMenuLabel.text = "Manage Account"
        }

        binding.rowLanguage.apply {
            ivMenuIcon.setImageResource(R.drawable.ic_globe)
            tvMenuLabel.text = "Language Settings"
        }

        binding.rowLogout.apply {
            ivMenuIcon.setImageResource(R.drawable.ic_logout_button)
            ivMenuIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.brand_brown))
            tvMenuLabel.text = "Logout"
            tvMenuLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_brown))
        }
    }

    private fun loadSellerProfile() {
        val uid = currentUid
        if (uid.isEmpty()) return

        authRepository.getSellerProfile(uid) { profile ->
            if (isAdded && profile != null) {
                binding.tvShopName.text = profile.businessName
                binding.tvSellerName.text = profile.ownerName
                binding.tvSellerLocation.text = "${profile.city}, ${profile.country}"
                binding.tvShopDescription.text = profile.description
                binding.tvSellerPhone.text = profile.phone
                binding.tvSellerEmail.text = profile.email

                if (profile.photoUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(profile.photoUrl)
                        .circleCrop()
                        .into(binding.ivSellerPhoto)

                    Glide.with(this)
                        .load(profile.photoUrl)
                        .circleCrop()
                        .into(binding.ivTopProfile)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnEditProfile.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(
                fragment = EditSellerProfileFragment(),
                addToBackStack = true,
            )
        }

        binding.rowEditShop.root.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(
                fragment = EditShopDetailsFragment(),
                addToBackStack = true,
            )
        }

        binding.rowManageAccount.root.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(
                fragment = ManageAccountFragment(),
                addToBackStack = true,
            )
        }

        binding.rowLanguage.root.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(
                fragment = LanguageSettingsFragment(),
                addToBackStack = true,
            )
        }

        binding.rowLogout.root.setOnClickListener {
            showLogoutDialog()
        }

        binding.btnUpgrade.setOnClickListener {
            Toast.makeText(requireContext(), "Upgrade coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutDialog() {
        val dialog = LogoutBottomSheetFragment()
        dialog.show(parentFragmentManager, "logout_dialog")
    }

    private fun setupBottomNavigation() {
        val nav = binding.includeBottomNav
        resetAllNavItems()
        updateNavItemVisuals(nav.ivNavProfile, nav.tvNavProfile, true)

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
        nav.navInventory.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerInventoryFragment(), false)
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
        val color = if (isSelected) R.color.brand_orange else R.color.nav_inactive_content
        icon.setColorFilter(ContextCompat.getColor(context, color))
        label.setTextColor(ContextCompat.getColor(context, color))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingVar = null
    }
}
