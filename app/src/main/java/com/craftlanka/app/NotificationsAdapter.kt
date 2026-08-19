package com.craftlanka.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

enum class NotificationType {
    ORDER_UPDATE,
    MESSAGE,
    PROMOTION,
    PRICE_DROP,
    FEEDBACK,
    HEADER,
}

data class NotificationData(
    val id: Int,
    val type: NotificationType,
    val category: String,
    val time: String,
    val content: String,
    val extraText: String? = null,
    val isHeader: Boolean = false,
    val headerTitle: String? = null,
)

class NotificationsAdapter(private val notifications: List<NotificationData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (notifications[position].isHeader) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHeader: TextView = view.findViewById(android.R.id.text1)
    }

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cvIconBadge: MaterialCardView = view.findViewById(R.id.cvIconBadge)
        val ivIcon: ImageView = view.findViewById(R.id.ivNotificationIcon)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val cvActionBadge: MaterialCardView = view.findViewById(R.id.cvActionBadge)
        val tvActionText: TextView = view.findViewById(R.id.tvActionText)
        val cvMessagePreview: MaterialCardView = view.findViewById(R.id.cvMessagePreview)
        val tvMessagePreview: TextView = view.findViewById(R.id.tvMessagePreview)
        val llRatingBar: LinearLayout = view.findViewById(R.id.llRatingBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notification, parent, false)
            NotificationViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = notifications[position]
        if (holder is HeaderViewHolder) {
            holder.tvHeader.text = item.headerTitle
            holder.tvHeader.textSize = 12f
            holder.tvHeader.setPadding(48, 48, 0, 16)
            holder.tvHeader.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.muted_gray))
            holder.tvHeader.setTypeface(null, android.graphics.Typeface.BOLD)
        } else if (holder is NotificationViewHolder) {
            holder.tvCategory.text = item.category
            holder.tvTime.text = item.time
            holder.tvContent.text = item.content

            val context = holder.itemView.context

            // Reset visibilities
            holder.cvActionBadge.visibility = View.GONE
            holder.cvMessagePreview.visibility = View.GONE
            holder.llRatingBar.visibility = View.GONE

            when (item.type) {
                NotificationType.ORDER_UPDATE -> {
                    holder.cvIconBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status_delivered_bg))
                    holder.ivIcon.setImageResource(R.drawable.ic_nav_orders)
                    holder.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.status_delivered_text))
                    holder.tvCategory.setTextColor(ContextCompat.getColor(context, R.color.status_delivered_text))
                    holder.cvActionBadge.visibility = View.VISIBLE
                    holder.tvActionText.text = "Track shipment >"
                }
                NotificationType.MESSAGE -> {
                    holder.cvIconBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_gray_pill))
                    holder.ivIcon.setImageResource(R.drawable.ic_nav_profile)
                    holder.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.charcoal))
                    holder.tvCategory.setTextColor(ContextCompat.getColor(context, R.color.charcoal))
                    holder.cvMessagePreview.visibility = View.VISIBLE
                    holder.tvMessagePreview.text = item.extraText
                }
                NotificationType.PROMOTION -> {
                    holder.cvIconBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.promo_blue_bg))
                    holder.ivIcon.setImageResource(R.drawable.ic_nav_cart) // Use tag icon if available
                    holder.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.promo_blue_icon))
                    holder.tvCategory.setTextColor(ContextCompat.getColor(context, R.color.promo_blue_icon))
                }
                NotificationType.PRICE_DROP -> {
                    holder.cvIconBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.price_drop_bg))
                    holder.ivIcon.setImageResource(R.drawable.ic_heart_outline)
                    holder.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.price_drop_icon))
                    holder.tvCategory.setTextColor(ContextCompat.getColor(context, R.color.price_drop_icon))
                }
                NotificationType.FEEDBACK -> {
                    holder.cvIconBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status_delivered_bg))
                    holder.ivIcon.setImageResource(android.R.drawable.btn_star_big_on)
                    holder.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.status_delivered_text))
                    holder.tvCategory.setTextColor(ContextCompat.getColor(context, R.color.status_delivered_text))
                    holder.llRatingBar.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
    }

    override fun getItemCount() = notifications.size
}
