package com.craftlanka.app.seller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.craftlanka.app.R
import com.craftlanka.app.databinding.ItemSellerProductBinding
import com.craftlanka.app.model.Product

class SellerProductsAdapter(
    private var products: List<Product>,
    private val onUpdateClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit,
) : RecyclerView.Adapter<SellerProductsAdapter.ProductViewHolder>() {
    class ProductViewHolder(val binding: ItemSellerProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProductViewHolder {
        val binding =
            ItemSellerProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int,
    ) {
        val product = products[position]
        val context = holder.itemView.context

        holder.binding.apply {
            tvProductName.text = product.productName
            tvProductPrice.text = "LKR ${String.format("%,.0f", product.price)}"
            tvProductCategoryTag.text = product.category

            // Stock Status Logic: Less than 5 is "Low Stock"
            if (product.stockQuantity < 5) {
                tvStockStatus.text = "Low Stock (${product.stockQuantity})"
                tvStockStatus.setTextColor(ContextCompat.getColor(context, R.color.text_red))
                ivStockIcon.setImageResource(R.drawable.ic_warning_circle_red)
                ivStockIcon.clearColorFilter() // Use original SVG colors if applicable
            } else {
                tvStockStatus.text = "In Stock (${product.stockQuantity})"
                tvStockStatus.setTextColor(ContextCompat.getColor(context, R.color.text_grey))
                ivStockIcon.setImageResource(R.drawable.ic_check_circle_green)
                ivStockIcon.clearColorFilter()
            }

            // Category tag style
            when (product.category.lowercase()) {
                "woodwork" -> {
                    tvProductCategoryTag.backgroundTintList = ContextCompat.getColorStateList(context, R.color.warning_bg)
                    tvProductCategoryTag.setTextColor(ContextCompat.getColor(context, R.color.status_pending_text))
                }
                "pottery" -> {
                    tvProductCategoryTag.backgroundTintList = ContextCompat.getColorStateList(context, R.color.icon_bg_green)
                    tvProductCategoryTag.setTextColor(ContextCompat.getColor(context, R.color.brand_green))
                }
            }

            // Load real product image
            Glide.with(context)
                .load(product.imageUrl)
                .placeholder(R.drawable.ic_craftlanka_logo)
                .into(ivProductImage)

            btnUpdateProduct.setOnClickListener { onUpdateClick(product) }
            btnDeleteProduct.setOnClickListener { onDeleteClick(product) }
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
