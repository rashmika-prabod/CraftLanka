package com.craftlanka.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.craftlanka.app.admin.AdminLoginFragment
import com.craftlanka.app.buyer.BuyerLoginFragment
import com.craftlanka.app.databinding.FragmentSplashBinding
import com.craftlanka.app.seller.SellerLoginFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            delay(2500) // Display splash screen animation for 2.5 seconds

            if (isAdded) {
                navigateNextScreen()
            }
        }
    }

    private fun navigateNextScreen() {
        val mainActivity = requireActivity() as MainActivity
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        val userRole = prefs.getString("user_role", null)

        val targetFragment: Fragment = when (userRole) {
            "admin" -> {
                val isAdminLoggedIn = prefs.getBoolean("admin_is_logged_in", false)
                if (isAdminLoggedIn) {
                    // TODO: Replace with AdminDashboardFragment() when created
                    AdminLoginFragment()
                } else {
                    AdminLoginFragment()
                }
            }
            "seller" -> {
                val isSellerRemembered = prefs.getBoolean("seller_remember_me", false)
                val loggedSeller = prefs.getString("logged_seller", null)
                if (isSellerRemembered && !loggedSeller.isNullOrEmpty()) {
                    // TODO: Replace with SellerDashboardFragment() when created
                    SellerLoginFragment()
                } else {
                    SellerLoginFragment()
                }
            }
            "buyer" -> {
                val isBuyerRemembered = prefs.getBoolean("remember_me", false)
                val loggedBuyer = prefs.getString("logged_user", null)
                if (isBuyerRemembered && !loggedBuyer.isNullOrEmpty()) {
                    // TODO: Replace with BuyerDashboardFragment() / HomeFragment() when created
                    BuyerLoginFragment()
                } else {
                    BuyerLoginFragment()
                }
            }
            else -> {
                // First-time launch or unselected role -> Onboarding Flow
                OnboardingFragment()
            }
        }

        mainActivity.navigationManager.replaceFragment(
            fragment = targetFragment,
            addToBackStack = false
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}