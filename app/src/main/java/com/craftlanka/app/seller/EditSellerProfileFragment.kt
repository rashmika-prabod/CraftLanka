package com.craftlanka.app.seller

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.craftlanka.app.data.AuthRepository
import com.craftlanka.app.databinding.FragmentEditSellerProfileBinding
import com.craftlanka.app.model.SellerProfile
import com.google.firebase.auth.FirebaseAuth

class EditSellerProfileFragment : Fragment() {

    private var _binding: FragmentEditSellerProfileBinding? = null
    val binding get() = _binding!!

    private val authRepository = AuthRepository()
    private var currentProfile: SellerProfile? = null
    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivSellerPhoto.setImageURI(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditSellerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadCurrentProfile()
        setupListeners()
    }

    private fun loadCurrentProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        authRepository.getSellerProfile(uid) { profile ->
            if (isAdded && profile != null) {
                currentProfile = profile
                binding.apply {
                    etBusinessName.setText(profile.businessName)
                    etBusinessDescription.setText(profile.description)
                    etOwnerName.setText(profile.ownerName)
                    etAddressNo.setText(profile.addressNo)
                    etRoad.setText(profile.road)
                    etCity.setText(profile.city)
                    etCountry.setText(profile.country)
                    etEmail.setText(profile.email)

                    if (profile.photoUrl.isNotEmpty()) {
                        Glide.with(this@EditSellerProfileFragment)
                            .load(profile.photoUrl)
                            .circleCrop()
                            .into(ivSellerPhoto)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }

        binding.btnChangePhoto.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val profile = currentProfile ?: return

        val updatedProfile = profile.copy(
            businessName = binding.etBusinessName.text.toString(),
            description = binding.etBusinessDescription.text.toString(),
            ownerName = binding.etOwnerName.text.toString(),
            addressNo = binding.etAddressNo.text.toString(),
            road = binding.etRoad.text.toString(),
            city = binding.etCity.text.toString(),
            country = binding.etCountry.text.toString(),
            email = binding.etEmail.text.toString(),
        )

        binding.btnSave.isEnabled = false

        if (selectedImageUri != null) {
            authRepository.uploadPhoto(
                requireContext(),
                selectedImageUri!!,
                onSuccess = { url ->
                    updateProfileInFirestore(updatedProfile.copy(photoUrl = url))
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "Photo upload failed: $error", Toast.LENGTH_SHORT).show()
                    binding.btnSave.isEnabled = true
                },
            )
        } else {
            updateProfileInFirestore(updatedProfile)
        }
    }

    private fun updateProfileInFirestore(profile: SellerProfile) {
        authRepository.updateSellerProfile(
            profile,
            onSuccess = {
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), "Update failed: $error", Toast.LENGTH_SHORT).show()
                binding.btnSave.isEnabled = true
            },
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
