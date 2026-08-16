package com.craftlanka.app.seller

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.craftlanka.app.MainActivity
import com.craftlanka.app.databinding.FragmentSellerLoginBinding

class SellerLoginFragment : Fragment() {

    private var _binding: FragmentSellerLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Login Action
        binding.btnLogin.setOnClickListener {
            if (validateInput()) {
                val identifier = binding.etIdentifier.text.toString().trim()
                val rememberMe = binding.cbRememberMe.isChecked

                val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                prefs.edit {
                    putString("logged_seller", identifier)
                    putBoolean("seller_remember_me", rememberMe)
                    putString("user_role", "seller")
                }

                Toast.makeText(requireContext(), "Seller login successful!", Toast.LENGTH_SHORT).show()
                // TODO: Navigate to Seller Dashboard
            }
        }

        // Forgot Password Action
        binding.btnForgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Forgot Password clicked", Toast.LENGTH_SHORT).show()
        }

        // Redirect to Seller Registration Screen
        binding.btnGotoRegister.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.navigationManager.replaceFragment(
                fragment = SellerRegisterFragment(),
                addToBackStack = true
            )
        }
    }

    private fun validateInput(): Boolean {
        val identifier = binding.etIdentifier.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (identifier.isEmpty()) {
            binding.tilIdentifier.error = "Please enter your email or phone number"
            return false
        }
        binding.tilIdentifier.error = null

        if (password.isEmpty()) {
            binding.tilPassword.error = "Please enter your password"
            return false
        }
        binding.tilPassword.error = null

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}