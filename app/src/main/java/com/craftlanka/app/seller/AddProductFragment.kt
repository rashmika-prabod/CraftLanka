package com.craftlanka.app.seller

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.craftlanka.app.R
import com.craftlanka.app.data.SellerRepository
import com.craftlanka.app.databinding.FragmentAddProductBinding
import com.craftlanka.app.model.Product
import com.google.firebase.auth.FirebaseAuth

class AddProductFragment : Fragment() {
    private var bindingVar: FragmentAddProductBinding? = null
    private val binding get() = bindingVar!!

    private val sellerRepository = SellerRepository()
    private val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private var selectedImageUri: Uri? = null

    // Photo picker launcher for a single image selection
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedImageUri = uri

                // UI Update: Show the selected image full-width and hide instructions
                binding.ivProductImageFull.setImageURI(uri)
                binding.ivProductImageFull.visibility = View.VISIBLE
                binding.layoutUploadInstructions.visibility = View.GONE
            } else {
                Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupCategoryDropdown()
        setupButtons()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupCategoryDropdown() {
        val categories = resources.getStringArray(R.array.product_categories)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)
    }

    private fun setupButtons() {
        // Trigger photo picker on card click
        binding.cardUploadPhoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Publish Action
        binding.btnPublish.setOnClickListener {
            if (validateForm()) {
                if (selectedImageUri == null) {
                    Toast.makeText(requireContext(), "Please select a product photo", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                performPublish()
            }
        }

        // Draft Action
        binding.btnSaveDraft.setOnClickListener {
            Toast.makeText(requireContext(), "Product saved as draft", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performPublish() {
        val name = binding.etProductName.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        val category = binding.actvCategory.text.toString().trim()
        val price = binding.etPrice.text.toString().trim().toDoubleOrNull() ?: 0.0
        val stock = binding.etStock.text.toString().trim().toIntOrNull() ?: 0

        // 1. Disable button to prevent multiple clicks
        binding.btnPublish.isEnabled = false
        Toast.makeText(requireContext(), "Processing your product...", Toast.LENGTH_SHORT).show()

        // 2. Upload image to Cloudinary first
        sellerRepository.uploadProductImage(
            context = requireContext(),
            imageUri = selectedImageUri!!,
            onSuccess = { imageUrl ->

                // 3. Create product object with the secure URL
                val product =
                    Product(
                        sellerUid = currentUid,
                        productName = name,
                        description = desc,
                        category = category,
                        price = price,
                        stockQuantity = stock,
                        imageUrl = imageUrl,
                    )

                // 4. Save to Firestore
                sellerRepository.addProduct(
                    product = product,
                    onSuccess = {
                        Toast.makeText(requireContext(), "Product published successfully!", Toast.LENGTH_LONG).show()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    },
                    onFailure = { error ->
                        binding.btnPublish.isEnabled = true
                        Toast.makeText(requireContext(), "Database error: $error", Toast.LENGTH_LONG).show()
                    },
                )
            },
            onFailure = { error ->
                binding.btnPublish.isEnabled = true
                Toast.makeText(requireContext(), "Image upload failed: $error", Toast.LENGTH_LONG).show()
            },
        )
    }

    private fun validateForm(): Boolean {
        val name = binding.etProductName.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        val category = binding.actvCategory.text.toString().trim()
        val price = binding.etPrice.text.toString().trim()
        val stock = binding.etStock.text.toString().trim()

        var isValid = true

        if (name.isEmpty()) {
            binding.tilProductName.error = "Name is required"
            isValid = false
        } else {
            binding.tilProductName.error = null
        }

        if (desc.isEmpty()) {
            binding.tilDescription.error = "Description is required"
            isValid = false
        } else {
            binding.tilDescription.error = null
        }

        if (category.isEmpty() || category == "Select a category") {
            binding.tilCategory.error = "Please select a category"
            isValid = false
        } else {
            binding.tilCategory.error = null
        }

        if (price.isEmpty()) {
            binding.tilPrice.error = "Price is required"
            isValid = false
        } else {
            binding.tilPrice.error = null
        }

        if (stock.isEmpty()) {
            binding.tilStock.error = "Stock is required"
            isValid = false
        } else {
            binding.tilStock.error = null
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingVar = null
    }
}
