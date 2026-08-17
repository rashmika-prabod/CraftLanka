package com.craftlanka.app.buyer

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.craftlanka.app.MainActivity
import com.craftlanka.app.data.AuthRepository
import com.craftlanka.app.databinding.FragmentBuyerRegisterBinding
import com.craftlanka.app.model.BuyerProfile
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class BuyerRegisterFragment : Fragment() {
    private var bindingVar: FragmentBuyerRegisterBinding? = null
    private val binding get() = bindingVar!!

    // Step 4: Create a reference to our Auth Messenger
    private val authRepository = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentBuyerRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupTermsText()

        // Top Back Arrow Button
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Redirect to Login Screen
        binding.btnGotoLogin.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.navigationManager.replaceFragment(
                fragment = BuyerLoginFragment(),
                addToBackStack = false,
            )
        }

        // Create Account Action
        binding.btnCreateAccount.setOnClickListener {
            if (validateForm()) {
                // 1. Get the values from the boxes
                val fullName = binding.etFullName.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val phone = binding.etPhone.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()

                // 2. Create the Buyer Profile
                val profile =
                    BuyerProfile(
                        fullName = fullName,
                        email = email,
                        phone = phone,
                    )

                // 3. Disable button so the user doesn't click twice
                binding.btnCreateAccount.isEnabled = false

                // 4. Tell the messenger to register the buyer in Firebase
                authRepository.registerBuyer(
                    profile = profile,
                    password = password,
                    onSuccess = {
                        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        prefs.edit {
                            putString("user_role", "buyer")
                        }

                        // Navigate directly to RegistrationSuccessFragment
                        val mainActivity = requireActivity() as MainActivity
                        mainActivity.navigationManager.replaceFragment(
                            fragment = RegistrationSuccessFragment(),
                            addToBackStack = false,
                        )
                    },
                    onFailure = { errorMessage ->
                        // If it fails, let the user try again
                        binding.btnCreateAccount.isEnabled = true
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                    },
                )
            }
        }
    }

    private fun setupTermsText() {
        val fullText = "I agree to the Terms & Conditions and Privacy Policy."
        val spannable = SpannableString(fullText)
        val brandBrown = "#8C3B00".toColorInt()

        // 1. Clickable Terms & Conditions Link
        val termsStart = fullText.indexOf("Terms & Conditions")
        if (termsStart != -1) {
            val termsEnd = termsStart + "Terms & Conditions".length
            val termsClickable =
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        showPolicyDialog(
                            title = "Terms & Conditions",
                            message =
                            """
                                Welcome to CraftLanka! By registering as a buyer, you agree to:
                                
                                1. Maintain valid account credentials.
                                2. Engage respectfully with local Sri Lankan artisans.
                                3. Honor completed transaction agreements and orders.
                                4. Use the platform in accordance with local laws and regulations.
                            """.trimIndent(),
                        )
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = brandBrown
                        ds.isUnderlineText = true
                    }
                }
            spannable.setSpan(termsClickable, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // 2. Clickable Privacy Policy Link
        val privacyStart = fullText.indexOf("Privacy Policy")
        if (privacyStart != -1) {
            val privacyEnd = privacyStart + "Privacy Policy".length
            val privacyClickable =
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        showPolicyDialog(
                            title = "Privacy Policy",
                            message =
                            """
                                CraftLanka respects your privacy:
                                
                                1. We collect minimal information (Name, Email, Phone) necessary to fulfill orders.
                                2. Your data is encrypted and secure.
                                3. We never share your personal information with third parties without your explicit consent.
                            """.trimIndent(),
                        )
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = brandBrown
                        ds.isUnderlineText = true
                    }
                }
            spannable.setSpan(privacyClickable, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.tvTermsPolicy.text = spannable
        binding.tvTermsPolicy.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun showPolicyDialog(
        title: String,
        message: String,
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun validateForm(): Boolean {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (fullName.isEmpty()) {
            binding.tilFullName.error = "Full name is required"
            return false
        }
        binding.tilFullName.error = null

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email address is required"
            return false
        }
        binding.tilEmail.error = null

        if (phone.isEmpty()) {
            binding.tilPhone.error = "Phone number is required"
            return false
        }
        binding.tilPhone.error = null

        if (password.length < 8) {
            binding.tilPassword.error = "Password must be at least 8 characters"
            return false
        }
        binding.tilPassword.error = null

        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            return false
        }
        binding.tilConfirmPassword.error = null

        if (!binding.cbTerms.isChecked) {
            Toast.makeText(requireContext(), "Please accept the Terms & Conditions", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingVar = null
    }
}
