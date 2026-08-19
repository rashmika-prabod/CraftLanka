package com.craftlanka.app.seller

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.craftlanka.app.R
import com.craftlanka.app.data.AuthRepository
import com.craftlanka.app.databinding.FragmentEditShopDetailsBinding
import com.craftlanka.app.model.SellerProfile
import com.google.firebase.auth.FirebaseAuth

class EditShopDetailsFragment : Fragment() {

    private var _binding: FragmentEditShopDetailsBinding? = null
    val binding get() = _binding!!

    private val authRepository = AuthRepository()
    private var currentProfile: SellerProfile? = null
    private var selectedBannerUri: Uri? = null

    private val getBanner = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedBannerUri = it
            binding.ivShopBanner.visibility = View.VISIBLE
            binding.ivShopBanner.setImageURI(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditShopDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCitySpinner()
        loadCurrentProfile()
        setupListeners()
    }

    private fun setupCitySpinner() {
        val cities = arrayOf("Select City", "Colombo", "Kandy", "Galle", "Matara", "Jaffna", "Negombo", "Kurunegala", "Ratnapura", "Kegalle")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, cities)
        binding.spinnerCity.adapter = adapter
    }

    private fun loadCurrentProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        authRepository.getSellerProfile(uid) { profile ->
            if (isAdded && profile != null) {
                currentProfile = profile
                binding.apply {
                    etShopName.setText(profile.businessName)
                    etShopDescription.setText(profile.description)
                    etPhone.setText(profile.phone)
                    etEmail.setText(profile.email)

                    // Set spinner selection
                    val cities = resources.getStringArray(R.array.cities_array) // Assuming you have this or use the one from setupCitySpinner
                    // For now use a simple index search
                    val adapter = spinnerCity.adapter as ArrayAdapter<String>
                    val position = adapter.getPosition(profile.city)
                    if (position >= 0) spinnerCity.setSelection(position)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }

        binding.btnUpdateBanner.setOnClickListener {
            getBanner.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val profile = currentProfile ?: return

        val updatedProfile = profile.copy(
            businessName = binding.etShopName.text.toString(),
            description = binding.etShopDescription.text.toString(),
            city = binding.spinnerCity.selectedItem.toString(),
            phone = binding.etPhone.text.toString(),
            email = binding.etEmail.text.toString(),
        )

        binding.btnSave.isEnabled = false

        // In a real app, you might upload a separate bannerUrl field
        // For now, let's just update the profile
        updateProfileInFirestore(updatedProfile)
    }

    private fun updateProfileInFirestore(profile: SellerProfile) {
        authRepository.updateSellerProfile(
            profile,
            onSuccess = {
                Toast.makeText(requireContext(), "Shop details updated", Toast.LENGTH_SHORT).show()
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
