package com.craftlanka.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

enum class OrderStatus {
    ALL,
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
}

data class OrderData(
    val id: String,
    val date: String,
    val customerName: String,
    val productName: String,
    val quantity: String,
    val price: String,
    val status: OrderStatus,
)

class OrdersAdapter(
    private var orders: List<OrderData>,
    private val onItemClick: (OrderData) -> Unit,
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvCustomerName: TextView = view.findViewById(R.id.tvCustomerName)
        val tvProductName: TextView = view.findViewById(R.id.tvProductName)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvStatusBadge: TextView = view.findViewById(R.id.tvStatusBadge)
        val ivProductThumb: ShapeableImageView = view.findViewById(R.id.ivProductThumb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvOrderId.text = order.id
        holder.tvOrderDate.text = order.date
        holder.tvCustomerName.text = order.customerName
        holder.tvProductName.text = order.productName
        holder.tvQuantity.text = order.quantity
        holder.tvPrice.text = order.price
        holder.tvStatusBadge.text = order.status.name

        val context = holder.itemView.context

        holder.itemView.setOnClickListener { onItemClick(order) }

        when (order.status) {
            OrderStatus.PENDING -> {
                holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_pending)
                holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_pending_text))
            }
            OrderStatus.PROCESSING -> {
                holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_processing)
                holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_processing_text))
            }
            OrderStatus.SHIPPED -> {
                holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_shipped)
                holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_shipped_text))
            }
            OrderStatus.DELIVERED -> {
                holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_delivered)
                holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_delivered_text))
            }
            OrderStatus.CANCELLED -> {
                holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_cancelled)
                holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_cancelled_text))
            }
            else -> {}
        }
    }

    override fun getItemCount() = orders.size

    fun updateOrders(newOrders: List<OrderData>) {
        this.orders = newOrders
        notifyDataSetChanged()
    }
}
