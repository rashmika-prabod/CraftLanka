package com.craftlanka.app.seller

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.craftlanka.app.MainActivity
import com.craftlanka.app.data.AuthRepository
import com.craftlanka.app.databinding.FragmentSellerLoginBinding
import com.google.firebase.auth.FirebaseAuth

class SellerLoginFragment : Fragment() {
    private var bindingVar: FragmentSellerLoginBinding? = null
    private val binding get() = bindingVar!!

    private val authRepository = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentSellerLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Login Action
        binding.btnLogin.setOnClickListener {
            if (validateInput()) {
                performSellerLogin()
            }
        }

        // Forgot Password Action
        binding.btnForgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Forgot Password feature coming soon", Toast.LENGTH_SHORT).show()
        }

        // Redirect to Seller Registration Screen
        binding.btnGotoRegister.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(
                fragment = SellerRegisterFragment(),
                addToBackStack = true,
            )
        }
    }

    private fun performSellerLogin() {
        val email = binding.etIdentifier.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val rememberMe = binding.cbRememberMe.isChecked

        binding.btnLogin.isEnabled = false
        Toast.makeText(requireContext(), "Logging you in...", Toast.LENGTH_SHORT).show()

        // 1. Authenticate with Firebase
        authRepository.loginUser(
            email = email,
            password = password,
            onSuccess = { role ->
                if (!isAdded) return@loginUser

                // 2. Verify Seller Role
                if (role == "seller") {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    
                    // 3. Fetch Profile for personalization
                    authRepository.getSellerProfile(uid) { profile ->
                        if (!isAdded) return@getSellerProfile

                        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        prefs.edit {
                            putString("logged_seller", email)
                            putBoolean("seller_remember_me", rememberMe)
                            putString("user_role", "seller")
                            putString("seller_business_name", profile?.businessName ?: "")
                        }

                        Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                        binding.btnLogin.isEnabled = true

                        // REDIRECTION FIX: Ensure navigation happens correctly
                        (activity as? MainActivity)?.navigationManager?.replaceFragment(
                            fragment = SellerHomeFragment(),
                            addToBackStack = false,
                            clearBackStack = true,
                        )
                    }
                } else {
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), "This account is not a Seller account.", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                }
            },
            onFailure = { errorMessage ->
                if (isAdded) {
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), "Login failed: $errorMessage", Toast.LENGTH_LONG).show()
                }
            },
        )
    }

    private fun validateInput(): Boolean {
        val identifier = binding.etIdentifier.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (identifier.isEmpty()) {
            binding.tilIdentifier.error = "Email is required"
            return false
        }
        binding.tilIdentifier.error = null

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            return false
        }
        binding.tilPassword.error = null

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingVar = null
    }
}
