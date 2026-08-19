package com.craftlanka.app.seller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.craftlanka.app.R
import com.craftlanka.app.databinding.ItemInventoryActionRequiredBinding
import com.craftlanka.app.model.Product

class SellerInventoryAdapter(
    private var products: List<Product>,
    private val onUpdateStockClick: (Product) -> Unit,
) : RecyclerView.Adapter<SellerInventoryAdapter.InventoryViewHolder>() {

    class InventoryViewHolder(val binding: ItemInventoryActionRequiredBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemInventoryActionRequiredBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return InventoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val product = products[position]
        holder.binding.apply {
            tvProductName.text = product.productName
            tvSku.text = "SKU: ${product.productId.takeLast(8).uppercase()}"
            tvStockLeft.text = "${product.stockQuantity} left"

            Glide.with(root.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.ic_craftlanka_logo)
                .into(ivProductImage)

            btnUpdateStock.setOnClickListener { onUpdateStockClick(product) }
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
