package com.craftlanka.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.craftlanka.app.databinding.ItemCategoryChipBinding

class CategoryAdapter(
    private val categories: List<String>,
    private val onCategorySelected: (String) -> Unit,
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    class CategoryViewHolder(val binding: ItemCategoryChipBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.tvCategoryName.text = category

        val isSelected = selectedPosition == position

        // Update styling based on selection
        holder.binding.root.setCardBackgroundColor(
            if (isSelected) {
                ContextCompat.getColor(holder.binding.root.context, R.color.black)
            } else {
                ContextCompat.getColor(holder.binding.root.context, R.color.white)
            },
        )
        holder.binding.tvCategoryName.setTextColor(
            if (isSelected) {
                ContextCompat.getColor(holder.binding.root.context, R.color.white)
            } else {
                ContextCompat.getColor(holder.binding.root.context, R.color.muted_gray)
            },
        )
        holder.binding.root.strokeWidth = if (isSelected) 0 else 1

        holder.binding.root.setOnClickListener {
            if (selectedPosition != holder.adapterPosition) {
                val oldPos = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(oldPos)
                notifyItemChanged(selectedPosition)
                onCategorySelected(category)
            }
        }
    }

    override fun getItemCount() = categories.size
}
