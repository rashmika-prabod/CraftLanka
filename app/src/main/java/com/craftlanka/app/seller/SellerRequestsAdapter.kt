package com.craftlanka.app.seller

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.craftlanka.app.R
import com.craftlanka.app.databinding.ItemRequestAcceptedBinding
import com.craftlanka.app.databinding.ItemRequestPendingBinding
import com.craftlanka.app.databinding.ItemRequestRejectedBinding
import com.craftlanka.app.model.BuyerRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SellerRequestsAdapter(
    private var requests: List<BuyerRequest>,
    private val onAccept: (BuyerRequest) -> Unit,
    private val onReject: (BuyerRequest) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_PENDING = 0
        private const val TYPE_ACCEPTED = 1
        private const val TYPE_REJECTED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (requests[position].status) {
            "ACCEPTED" -> TYPE_ACCEPTED
            "REJECTED" -> TYPE_REJECTED
            else -> TYPE_PENDING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_ACCEPTED -> AcceptedViewHolder(ItemRequestAcceptedBinding.inflate(inflater, parent, false))
            TYPE_REJECTED -> RejectedViewHolder(ItemRequestRejectedBinding.inflate(inflater, parent, false))
            else -> PendingViewHolder(ItemRequestPendingBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val request = requests[position]
        when (holder) {
            is PendingViewHolder -> holder.bind(request)
            is AcceptedViewHolder -> holder.bind(request)
            is RejectedViewHolder -> holder.bind(request)
        }
    }

    override fun getItemCount(): Int = requests.size

    fun updateData(newRequests: List<BuyerRequest>) {
        requests = newRequests
        notifyDataSetChanged()
    }

    inner class PendingViewHolder(private val binding: ItemRequestPendingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(request: BuyerRequest) {
            val context = binding.root.context
            binding.tvProductName.text = request.productName
            binding.tvQuantity.text = context.getString(R.string.format_quantity, request.quantity)
            binding.tvPrice.text = context.getString(R.string.format_lkr_price, request.price)
            binding.tvLocation.text = request.location
            binding.tvTime.text = formatRelativeTime(request.timestamp)

            Glide.with(context)
                .load(request.imageUrl)
                .placeholder(R.drawable.ic_craftlanka_logo)
                .into(binding.ivProductImage)

            binding.btnAccept.setOnClickListener { onAccept(request) }
            binding.btnReject.setOnClickListener { onReject(request) }
        }
    }

    inner class AcceptedViewHolder(private val binding: ItemRequestAcceptedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(request: BuyerRequest) {
            val context = binding.root.context
            binding.tvProductName.text = request.productName
            binding.tvQuantity.text = context.getString(R.string.format_quantity, request.quantity)
            binding.tvPrice.text = context.getString(R.string.format_lkr_price, request.price)
            binding.tvLocation.text = request.location
            binding.tvDate.text = formatDate(request.timestamp)

            Glide.with(context)
                .load(request.imageUrl)
                .placeholder(R.drawable.ic_craftlanka_logo)
                .into(binding.ivProductImage)
        }
    }

    inner class RejectedViewHolder(private val binding: ItemRequestRejectedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(request: BuyerRequest) {
            val context = binding.root.context
            binding.tvProductName.text = request.productName
            binding.tvQuantity.text = context.getString(R.string.format_quantity, request.quantity)
            binding.tvPrice.text = context.getString(R.string.format_lkr_price, request.price)
            binding.tvLocation.text = request.location
            binding.tvDate.text = formatDate(request.timestamp)
            binding.tvRejectionReason.text = context.getString(R.string.format_rejection_reason, request.rejectionReason)

            Glide.with(context)
                .load(request.imageUrl)
                .placeholder(R.drawable.ic_craftlanka_logo)
                .into(binding.ivProductImage)
        }
    }

    private fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        return if (DateUtils.isToday(timestamp)) {
            val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
            "Today, $time"
        } else if (DateUtils.isToday(timestamp + DateUtils.DAY_IN_MILLIS)) {
            val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
            "Yesterday, $time"
        } else {
            SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(timestamp))
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
