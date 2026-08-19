package com.craftlanka.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.craftlanka.app.databinding.ItemProductBinding

class ProductAdapter(
    private var products: List<Product>,
    private val onAddToCart: (Product) -> Unit,
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.binding.apply {
            tvProductName.text = product.title
            tvArtisan.text = product.artisan
            tvPrice.text = product.priceString
            tvRating.text = "${product.rating} (${product.reviews})"

            tvOutOfStock.visibility = if (product.inStock) View.GONE else View.VISIBLE
            btnAddToCart.isEnabled = product.inStock
            btnAddToCart.alpha = if (product.inStock) 1.0f else 0.5f

            btnAddToCart.setOnClickListener { onAddToCart(product) }

            if (product.imageUrl.isNotBlank()) {
                Glide.with(ivProductImage.context)
                    .load(product.imageUrl)
                    .centerCrop()
                    .into(ivProductImage)
            } else {
                Glide.with(ivProductImage.context).clear(ivProductImage)
                ivProductImage.setImageDrawable(null)
            }
        }
    }

    override fun getItemCount() = products.size

    fun updateProducts(newProducts: List<Product>) {
        this.products = newProducts
        notifyDataSetChanged()
    }
}
