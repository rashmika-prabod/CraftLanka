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
import com.craftlanka.app.seller.SellerHomeFragment
import com.craftlanka.app.seller.SellerLoginFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private var bindingVar: FragmentSplashBinding? = null
    private val binding get() = bindingVar!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Safe programmatic Lottie setup
        try {
            binding.lottieAnimation.setAnimation(R.raw.loading_spinner)
            binding.lottieAnimation.playAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        lifecycleScope.launch {
            delay(2000)
            if (isAdded) {
                navigateNextScreen()
            }
        }
    }

    private fun navigateNextScreen() {
        val mainActivity = requireActivity() as MainActivity
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        val userRole = prefs.getString("user_role", null)
        val selectedLanguage = prefs.getString("selected_language", null)

        val targetFragment: Fragment =
            when (userRole) {
                "admin" -> {
                    val isAdminLoggedIn = prefs.getBoolean("admin_is_logged_in", false)
                    if (isAdminLoggedIn) AdminLoginFragment() else AdminLoginFragment()
                }
                "seller" -> {
                    val isSellerRemembered = prefs.getBoolean("seller_remember_me", false)
                    val loggedSeller = prefs.getString("logged_seller", null)
                    if (isSellerRemembered && !loggedSeller.isNullOrEmpty()) {
                        SellerHomeFragment()
                    } else {
                        SellerLoginFragment()
                    }
                }
                "buyer" -> {
                    val isBuyerRemembered = prefs.getBoolean("remember_me", false)
                    val loggedBuyer = prefs.getString("logged_user", null)
                    if (isBuyerRemembered && !loggedBuyer.isNullOrEmpty()) {
                        // TODO: Change to BuyerHome/Dashboard when ready
                        BuyerLoginFragment()
                    } else {
                        BuyerLoginFragment()
                    }
                }
                else -> {
                    if (selectedLanguage != null) {
                        RoleSelectionFragment()
                    } else {
                        OnboardingFragment()
                    }
                }
            }

        mainActivity.navigationManager.replaceFragment(
            fragment = targetFragment,
            addToBackStack = false,
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingVar = null
    }
}
