package com.craftlanka.app.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.craftlanka.app.R
import com.craftlanka.app.data.SellerRepository
import com.craftlanka.app.databinding.FragmentUpdateStockBinding
import com.craftlanka.app.model.Product

class UpdateStockFragment : Fragment() {

    private var bindingVar: FragmentUpdateStockBinding? = null
    private val binding get() = bindingVar!!

    private val sellerRepository = SellerRepository()
    private var productId: String? = null
    private var currentProduct: Product? = null

    companion object {
        private const val ARG_PRODUCT_ID = "product_id"

        fun newInstance(productId: String): UpdateStockFragment {
            val fragment = UpdateStockFragment()
            val args = Bundle()
            args.putString(ARG_PRODUCT_ID, productId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productId = arguments?.getString(ARG_PRODUCT_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentUpdateStockBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        loadProductData()
        setupButtons()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadProductData() {
        productId?.let { id ->
            sellerRepository.getProduct(
                id,
                onSuccess = { product ->
                    if (isAdded && product != null) {
                        currentProduct = product
                        displayProductInfo(product)
                    }
                },
                onFailure = { error ->
                    if (isAdded) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                },
            )
        }
    }

    private fun displayProductInfo(product: Product) {
        binding.tvProductName.text = product.productName
        binding.tvCategory.text = product.category
        binding.tvCurrentStockBadge.text = "${product.stockQuantity} units in store"
        binding.tvLastUpdatedBadge.text = "Updated ${formatTimeAgo(product.lastUpdated)}"

        Glide.with(this)
            .load(product.imageUrl)
            .placeholder(R.drawable.ic_craftlanka_logo)
            .into(binding.ivProductImage)
    }

    private fun setupButtons() {
        binding.btnConfirmUpdate.setOnClickListener {
            val newStockStr = binding.etNewStock.text.toString()
            if (newStockStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a quantity", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val additionalStock = newStockStr.toIntOrNull() ?: 0
            currentProduct?.let { product ->
                val updatedProduct = product.copy(
                    stockQuantity = product.stockQuantity + additionalStock,
                    lastUpdated = System.currentTimeMillis(),
                )

                binding.btnConfirmUpdate.isEnabled = false
                sellerRepository.addProduct(
                    updatedProduct,
                    onSuccess = {
                        if (isAdded) {
                            Toast.makeText(requireContext(), "Stock updated successfully", Toast.LENGTH_SHORT).show()
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    },
                    onFailure = { error ->
                        if (isAdded) {
                            binding.btnConfirmUpdate.isEnabled = true
                            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                        }
                    },
                )
            }
        }

        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun formatTimeAgo(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        val days = diff / (1000 * 60 * 60 * 24)
        return when {
            days == 0L -> "Today"
            days == 1L -> "Yesterday"
            else -> "$days days ago"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingVar = null
    }
}
