package com.craftlanka.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.craftlanka.app.admin.AdminLoginFragment
import com.craftlanka.app.buyer.BuyerRegisterFragment
import com.craftlanka.app.databinding.FragmentRoleSelectionBinding
import com.craftlanka.app.seller.SellerRegisterFragment
import com.google.android.material.card.MaterialCardView

class RoleSelectionFragment : Fragment() {

    private var _binding: FragmentRoleSelectionBinding? = null
    private val binding get() = _binding!!

    // Default role: buyer
    private var selectedRole = "buyer"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoleSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Highlight buyer card by default
        selectRole("buyer")

        // Card Click Listeners
        binding.cardBuyer.setOnClickListener { selectRole("buyer") }
        binding.cardSeller.setOnClickListener { selectRole("seller") }

        // Navigation Actions for Buyer / Seller
        binding.btnContinue.setOnClickListener {
            saveRoleAndProceed(selectedRole)
        }

        // Navigation Action for Admin Login
        binding.btnAdminLogin.setOnClickListener {
            val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit {
                putString("user_role", "admin")
            }

            val mainActivity = requireActivity() as MainActivity
            mainActivity.navigationManager.replaceFragment(
                fragment = AdminLoginFragment(),
                addToBackStack = true
            )
        }
    }

    private fun selectRole(role: String) {
        selectedRole = role
        updateCardStyle(binding.cardBuyer, role == "buyer")
        updateCardStyle(binding.cardSeller, role == "seller")
    }

    private fun updateCardStyle(card: MaterialCardView, isSelected: Boolean) {
        if (isSelected) {
            card.strokeColor = "#0E3818".toColorInt()
            card.strokeWidth = dpToPx(2)
        } else {
            card.strokeColor = "#D0D0D0".toColorInt()
            card.strokeWidth = dpToPx(1)
        }
    }

    private fun saveRoleAndProceed(role: String) {
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit {
            putString("user_role", role)
        }

        val mainActivity = requireActivity() as MainActivity
        if (role == "buyer") {
            mainActivity.navigationManager.replaceFragment(
                fragment = BuyerRegisterFragment(),
                addToBackStack = true
            )
        } else {
            mainActivity.navigationManager.replaceFragment(
                fragment = SellerRegisterFragment(),
                addToBackStack = true
            )
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}