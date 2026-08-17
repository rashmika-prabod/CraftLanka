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

        val targetFragment: Fragment = when (userRole) {
            "admin" -> AdminLoginFragment()
            "seller" -> SellerLoginFragment()
            "buyer" -> BuyerLoginFragment()
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
            addToBackStack = false
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}