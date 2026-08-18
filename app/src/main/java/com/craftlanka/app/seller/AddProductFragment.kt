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
import com.bumptech.glide.Glide
import com.craftlanka.app.R
import com.craftlanka.app.data.SellerRepository
import com.craftlanka.app.databinding.FragmentAddProductBinding
import com.craftlanka.app.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddProductFragment : Fragment() {
    private var bindingVar: FragmentAddProductBinding? = null
    private val binding get() = bindingVar!!

    private val sellerRepository = SellerRepository()
    private val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private var selectedImageUri: Uri? = null
    private var existingProductId: String? = null
    private var existingImageUrl: String? = null

    companion object {
        private const val ARG_PRODUCT_ID = "product_id"

        fun newInstance(productId: String? = null): AddProductFragment {
            val fragment = AddProductFragment()
            val args = Bundle()
            args.putString(ARG_PRODUCT_ID, productId)
            fragment.arguments = args
            return fragment
        }
    }

    // Photo picker launcher for a single image selection
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedImageUri = uri

                // UI Update: Show the selected image full-width and hide instructions
                binding.ivProductImageFull.setImageURI(uri)
                binding.ivProductImageFull.visibility = View.VISIBLE
                binding.layoutUploadInstructions.visibility = View.GONE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        existingProductId = arguments?.getString(ARG_PRODUCT_ID)
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

        if (existingProductId != null) {
            loadExistingProductData()
            binding.tvTitle.text = "Update Product"
            binding.btnPublish.text = "UPDATE PRODUCT"
        }
    }

    private fun loadExistingProductData() {
        existingProductId?.let { id ->
            FirebaseFirestore.getInstance().collection("products").document(id).get()
                .addOnSuccessListener { document ->
                    val product = document.toObject(Product::class.java)
                    if (product != null && isAdded) {
                        binding.etProductName.setText(product.productName)
                        binding.etDescription.setText(product.description)
                        binding.actvCategory.setText(product.category, false)
                        binding.etPrice.setText(product.price.toString())
                        binding.etStock.setText(product.stockQuantity.toString())
                        
                        existingImageUrl = product.imageUrl
                        if (product.imageUrl.isNotEmpty()) {
                            binding.ivProductImageFull.visibility = View.VISIBLE
                            binding.layoutUploadInstructions.visibility = View.GONE
                            Glide.with(this).load(product.imageUrl).into(binding.ivProductImageFull)
                        }
                    }
                }
        }
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

        // Publish/Update Action
        binding.btnPublish.setOnClickListener {
            if (validateForm()) {
                if (selectedImageUri == null && existingImageUrl == null) {
                    Toast.makeText(requireContext(), "Please select a product photo", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                performSave()
            }
        }

        // Draft Action
        binding.btnSaveDraft.setOnClickListener {
            Toast.makeText(requireContext(), "Product saved as draft", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performSave() {
        val name = binding.etProductName.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        val category = binding.actvCategory.text.toString().trim()
        val price = binding.etPrice.text.toString().trim().toDoubleOrNull() ?: 0.0
        val stock = binding.etStock.text.toString().trim().toIntOrNull() ?: 0

        binding.btnPublish.isEnabled = false
        val message = if (existingProductId == null) "Publishing product..." else "Updating product..."
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

        if (selectedImageUri != null) {
            // New image selected, upload it first
            sellerRepository.uploadProductImage(
                context = requireContext(),
                imageUri = selectedImageUri!!,
                onSuccess = { imageUrl ->
                    saveProductToFirestore(name, desc, category, price, stock, imageUrl)
                },
                onFailure = { error ->
                    binding.btnPublish.isEnabled = true
                    Toast.makeText(requireContext(), "Image upload failed: $error", Toast.LENGTH_LONG).show()
                }
            )
        } else {
            // Use existing image URL
            saveProductToFirestore(name, desc, category, price, stock, existingImageUrl ?: "")
        }
    }

    private fun saveProductToFirestore(name: String, desc: String, category: String, price: Double, stock: Int, imageUrl: String) {
        val product = Product(
            productId = existingProductId ?: "",
            sellerUid = currentUid,
            productName = name,
            description = desc,
            category = category,
            price = price,
            stockQuantity = stock,
            imageUrl = imageUrl
        )

        sellerRepository.addProduct(
            product = product,
            onSuccess = {
                val successMsg = if (existingProductId == null) "Product published!" else "Product updated!"
                Toast.makeText(requireContext(), successMsg, Toast.LENGTH_LONG).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            },
            onFailure = { error ->
                binding.btnPublish.isEnabled = true
                Toast.makeText(requireContext(), "Database error: $error", Toast.LENGTH_LONG).show()
            }
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
