package com.craftlanka.app

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.craftlanka.app.databinding.ActivityUserProfileEditeBinding

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileEditeBinding
    private var selectedImageUri: Uri? = null

    // Photo Gallery Image Picker
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.imgProfile.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileEditeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Back arrow click
        binding.btnBack.setOnClickListener {
            finish()
        }
        // Trigger Image Picker on Profile Image or Camera Icon Click
        binding.imgProfile.setOnClickListener { openGallery() }
        binding.btnChangePhoto.setOnClickListener { openGallery() }
        // Save Changes validation & submit
        binding.btnSaveChanges.setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun saveProfileChanges() {
        val fullName = binding.etFullName.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()

        if (fullName.isEmpty()) {
            binding.etFullName.error = "Name cannot be empty"
            binding.etFullName.requestFocus()
            return
        }
        if (phoneNumber.isEmpty()) {
            binding.etPhoneNumber.error = "Phone number cannot be empty"
            binding.etPhoneNumber.requestFocus()
            return
        }

        // Success logic
        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
