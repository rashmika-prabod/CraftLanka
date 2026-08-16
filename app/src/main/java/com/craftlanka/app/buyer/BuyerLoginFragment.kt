package com.craftlanka.app.buyer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.craftlanka.app.MainActivity
import com.craftlanka.app.RoleSelectionFragment
import com.craftlanka.app.databinding.FragmentBuyerLoginBinding

class BuyerLoginFragment : Fragment() {

    private var _binding: FragmentBuyerLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyerLoginBinding.inflate(inflater, container, false)
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
                    putString("logged_user", identifier)
                    putBoolean("remember_me", rememberMe)
                }

                Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                // TODO: Navigate to Buyer Dashboard / Main Home Screen
            }
        }

        // Google Sign-In Action
        binding.btnGoogleLogin.setOnClickListener {
            Toast.makeText(requireContext(), "Google Sign-In initiated", Toast.LENGTH_SHORT).show()
        }

        // Forgot Password Action
        binding.btnForgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Forgot Password clicked", Toast.LENGTH_SHORT).show()
        }

        // Redirect to Account Creation Flow
        binding.btnGotoRegister.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.navigationManager.replaceFragment(
                fragment = RoleSelectionFragment(),
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