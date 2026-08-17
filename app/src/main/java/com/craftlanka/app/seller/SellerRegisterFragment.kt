package com.craftlanka.app.seller

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
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
                val ownerName = binding.etOwnerName.text.toString().trim()
                val phone = binding.etPhone.text.toString().trim()
                val businessName = binding.etBusinessName.text.toString().trim()
                val addressNo = binding.etAddressNo.text.toString().trim()
                val road = binding.etRoad.text.toString().trim()
                val city = binding.etCity.text.toString().trim()
                val country = binding.etCountry.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()

                binding.btnCreateAccount.isEnabled = false

                if (selectedImageUri != null) {
                    authRepository.uploadSellerPhoto(
                        context = requireContext(),
                        imageUri = selectedImageUri!!,
                        onSuccess = { uploadedUrl ->
                            Log.d("SellerRegister", "Cloudinary Upload Success: $uploadedUrl")

                            val profile =
                                SellerProfile(
                                    ownerName = ownerName,
                                    phone = phone,
                                    businessName = businessName,
                                    addressNo = addressNo,
                                    road = road,
                                    city = city,
                                    country = country,
                                    email = email,
                                    photoUrl = uploadedUrl,
                                )
                            performRegistration(profile, password)
                        },
                        onFailure = { error ->
                            binding.btnCreateAccount.isEnabled = true
                            Log.e("SellerRegister", "Cloudinary Upload Failed: $error")
                            Toast.makeText(requireContext(), "Photo upload failed: $error", Toast.LENGTH_LONG).show()
                        },
                    )
                } else {
                    val profile =
                        SellerProfile(
                            ownerName = ownerName,
                            phone = phone,
                            businessName = businessName,
                            addressNo = addressNo,
                            road = road,
                            city = city,
                            country = country,
                            email = email,
                            photoUrl = "",
                        )
                    performRegistration(profile, password)
                }
            }
        }
    }

    private fun performRegistration(
        profile: SellerProfile,
        password: String,
    ) {
        authRepository.registerSeller(
            profile = profile,
            password = password,
            onSuccess = {
                val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                prefs.edit { putString("user_role", "seller") }

                val shopButtonText = getString(R.string.btn_login_to_your_shop)
                val mainActivity = requireActivity() as MainActivity
                mainActivity.navigationManager.replaceFragment(
                    fragment = RegistrationSuccessFragment.newInstance(shopButtonText),
                    addToBackStack = false,
                )
            },
            onFailure = { errorMessage ->
                binding.btnCreateAccount.isEnabled = true
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
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
            val termsClickable =
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        showPolicyDialog(
                            title = "Seller Terms & Conditions",
                            message =
                            """
                                Welcome to CraftLanka Merchant Services! By creating a seller account, you agree to:
                                
                                1. List authentic, Sri Lankan handcrafted goods.
                                2. Accurately represent product quality, origin, and crafting methods.
                                3. Honor shipping deadlines and customer support commitments.
                                4. Comply with CraftLanka marketplace commission structures and policies.
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

        val privacyStart = fullText.indexOf("privacy policy")
        if (privacyStart != -1) {
            val privacyEnd = privacyStart + "privacy policy".length
            val privacyClickable =
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        showPolicyDialog(
                            title = "CraftLanka Seller Privacy Policy",
                            message =
                            """
                                CraftLanka Merchant Privacy Notice:
                                
                                1. Business registration data is stored securely to process store verification.
                                2. Payment processing details are handled through encrypted financial gateways.
                                3. Customer order data must be used strictly for order fulfillment.
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
        val ownerName = binding.etOwnerName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val businessName = binding.etBusinessName.text.toString().trim()
        val addressNo = binding.etAddressNo.text.toString().trim()
        val road = binding.etRoad.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val country = binding.etCountry.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (ownerName.isEmpty()) {
            binding.tilOwnerName.error = "Owner name is required"
            return false
        }
        binding.tilOwnerName.error = null

        if (phone.isEmpty()) {
            binding.tilPhone.error = "Phone number is required"
            return false
        }
        binding.tilPhone.error = null

        if (businessName.isEmpty()) {
            binding.tilBusinessName.error = "Business name is required"
            return false
        }
        binding.tilBusinessName.error = null

        if (addressNo.isEmpty()) {
            binding.tilAddressNo.error = "Address number is required"
            return false
        }
        binding.tilAddressNo.error = null

        if (road.isEmpty()) {
            binding.tilRoad.error = "Road is required"
            return false
        }
        binding.tilRoad.error = null

        if (city.isEmpty()) {
            binding.tilCity.error = "City is required"
            return false
        }
        binding.tilCity.error = null

        if (country.isEmpty()) {
            binding.tilCountry.error = "Country is required"
            return false
        }
        binding.tilCountry.error = null

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email address is required"
            return false
        }
        binding.tilEmail.error = null

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
