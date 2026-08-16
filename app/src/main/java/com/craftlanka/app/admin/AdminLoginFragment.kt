package com.craftlanka.app.admin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.craftlanka.app.databinding.FragmentAdminLoginBinding

class AdminLoginFragment : Fragment() {

    private var _binding: FragmentAdminLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Login Button Listener
        binding.btnLogin.setOnClickListener {
            if (validateInput()) {
                val email = binding.etEmail.text.toString().trim()
                val rememberMe = binding.cbRememberMe.isChecked

                val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                prefs.edit {
                    putString("user_role", "admin")
                    putString("logged_admin_email", email)
                    putBoolean("admin_is_logged_in", true)
                    putBoolean("admin_remember_me", rememberMe)
                }

                Toast.makeText(requireContext(), "Admin authentication successful!", Toast.LENGTH_SHORT).show()
                // TODO: Navigate to Admin Dashboard
            }
        }

        // Forgot Password Listener
        binding.btnForgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Admin password reset initiated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.tilEmail.error = "Please enter admin email"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Please enter a valid email address"
            return false
        }
        binding.tilEmail.error = null

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