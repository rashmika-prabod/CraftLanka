package com.craftlanka.app.seller

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.craftlanka.app.MainActivity
import com.craftlanka.app.R
import com.craftlanka.app.buyer.RegistrationSuccessFragment
import com.craftlanka.app.data.AuthRepository
import com.craftlanka.app.databinding.FragmentSellerRegisterBinding
import com.craftlanka.app.model.SellerProfile
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SellerRegisterFragment : Fragment() {
    private var bindingVar: FragmentSellerRegisterBinding? = null
    private val binding get() = bindingVar!!

    private val authRepository = AuthRepository()
    private var selectedImageUri: Uri? = null

    private val photoPickerLauncher =
        registerForActivityResult(
            ActivityResultContracts.PickVisualMedia(),
        ) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                binding.ivSellerPhoto.imageTintList = null
                binding.ivSellerPhoto.setPadding(0, 0, 0, 0)
                binding.ivSellerPhoto.setImageURI(uri)
                binding.ivSellerPhoto.scaleType = ImageView.ScaleType.CENTER_CROP

                val params = binding.ivSellerPhoto.layoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                binding.ivSellerPhoto.layoutParams = params
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentSellerRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupTermsText()

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.fabUploadPhoto.setOnClickListener { openPhotoPicker() }
        binding.cardSellerPhoto.setOnClickListener { openPhotoPicker() }

        binding.btnGotoLogin.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.navigationManager.replaceFragment(
                fragment = SellerLoginFragment(),
                addToBackStack = true,
            )
        }

        binding.btnCreateAccount.setOnClickListener {
            if (validateForm()) {
                performRegistration()
            }
        }
    }

    private fun performRegistration() {
        binding.btnCreateAccount.isEnabled = false
        Toast.makeText(requireContext(), "Creating your account...", Toast.LENGTH_SHORT).show()

        if (selectedImageUri != null) {
            authRepository.uploadPhoto(
                requireContext(),
                selectedImageUri!!,
                onSuccess = { photoUrl: String ->
                    registerWithProfile(photoUrl)
                },
                onFailure = { error: String ->
                    binding.btnCreateAccount.isEnabled = true
                    Toast.makeText(requireContext(), "Photo upload failed: $error", Toast.LENGTH_SHORT).show()
                },
            )
        } else {
            registerWithProfile("")
        }
    }

    private fun registerWithProfile(photoUrl: String) {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString() // NOT TRIMMED to match Login

        val profile = SellerProfile(
            ownerName = binding.etOwnerName.text.toString().trim(),
            phone = binding.etPhone.text.toString().trim(),
            businessName = binding.etBusinessName.text.toString().trim(),
            addressNo = binding.etAddressNo.text.toString().trim(),
            road = binding.etRoad.text.toString().trim(),
            city = binding.etCity.text.toString().trim(),
            country = binding.etCountry.text.toString().trim(),
            email = email,
            photoUrl = photoUrl,
        )

        authRepository.registerSeller(
            profile,
            password,
            onSuccess = {
                if (isAdded) {
                    val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    prefs.edit { putString("user_role", "seller") }

                    val mainActivity = requireActivity() as MainActivity
                    mainActivity.navigationManager.replaceFragment(
                        fragment = RegistrationSuccessFragment.newInstance(getString(R.string.btn_login_to_your_shop)),
                        addToBackStack = false,
                        clearBackStack = true,
                    )
                }
            },
            onFailure = { error ->
                if (isAdded) {
                    binding.btnCreateAccount.isEnabled = true
                    Toast.makeText(requireContext(), "Registration failed: $error", Toast.LENGTH_LONG).show()
                }
            },
        )
    }

    private fun openPhotoPicker() {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
        )
    }

    private fun setupTermsText() {
        val fullText = "I agree to the Terms & Conditions and privacy policy of CraftLanka."
        val spannable = SpannableString(fullText)
        val brandBrown = "#8C3B00".toColorInt()

        val termsStart = fullText.indexOf("Terms & Conditions")
        if (termsStart != -1) {
            val termsEnd = termsStart + "Terms & Conditions".length
            val termsClickable = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    showPolicyDialog("Seller Terms & Conditions", "1. List authentic goods...\n2. Support customers...")
                }
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = brandBrown
                    ds.isUnderlineText = true
                }
            }
            spannable.setSpan(termsClickable, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val privacyStart = fullText.indexOf("privacy policy")
        if (privacyStart != -1) {
            val privacyEnd = privacyStart + "privacy policy".length
            val privacyClickable = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    showPolicyDialog("Seller Privacy Policy", "Your data is stored securely...")
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

    private fun showPolicyDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun validateForm(): Boolean {
        val ownerName = binding.etOwnerName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (ownerName.isEmpty()) {
            binding.tilOwnerName.error = "Required"
            return false
        }
        if (email.isEmpty()) {
            binding.tilEmail.error = "Required"
            return false
        }
        if (password.length < 8) {
            binding.tilPassword.error = "Min 8 chars"
            return false
        }
        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Mismatch"
            return false
        }
        if (!binding.cbTerms.isChecked) {
            Toast.makeText(requireContext(), "Accept terms", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingVar = null
    }
}
