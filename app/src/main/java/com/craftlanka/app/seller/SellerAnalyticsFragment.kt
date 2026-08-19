package com.craftlanka.app.seller

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.craftlanka.app.MainActivity
import com.craftlanka.app.R
import com.craftlanka.app.data.AuthRepository
import com.craftlanka.app.data.SellerRepository
import com.craftlanka.app.databinding.FragmentSellerAnalyticsBinding
import com.craftlanka.app.databinding.ItemBestSellingProductBinding
import com.craftlanka.app.model.Product
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

class SellerAnalyticsFragment : Fragment() {

    private var bindingVar: FragmentSellerAnalyticsBinding? = null
    private val binding get() = bindingVar!!

    private val authRepository = AuthRepository()
    private val sellerRepository = SellerRepository()

    private val currentUid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private var currentProducts = listOf<Product>()

    private val createDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf"),
    ) { uri ->
        uri?.let {
            generatePdfReport(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentSellerAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomNavigation()
        loadSellerProfile()
        fetchAnalyticsData()

        binding.btnDownloadReport.setOnClickListener {
            if (currentProducts.isEmpty()) {
                Toast.makeText(requireContext(), "No data to export", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            createDocumentLauncher.launch("CraftLanka_Store_Report_${System.currentTimeMillis()}.pdf")
        }

        binding.btnProfileHeader.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(
                fragment = SellerProfileFragment(),
                addToBackStack = true,
            )
        }
    }

    private fun loadSellerProfile() {
        val uid = currentUid
        if (uid.isEmpty()) return
        authRepository.getSellerProfile(uid) { profile ->
            if (isAdded && profile != null && profile.photoUrl.isNotEmpty()) {
                Glide.with(this).load(profile.photoUrl).circleCrop().into(binding.ivHeaderProfile)
            }
        }
    }

    private fun fetchAnalyticsData() {
        val uid = currentUid
        if (uid.isEmpty()) return

        sellerRepository.getSellerProducts(
            uid,
            onSuccess = { products ->
                if (isAdded) {
                    currentProducts = products
                    displayAnalytics(products)
                }
            },
            onFailure = { error ->
                if (isAdded) Toast.makeText(requireContext(), "Failed to fetch data: $error", Toast.LENGTH_SHORT).show()
            },
        )
    }

    private fun displayAnalytics(products: List<Product>) {
        val totalViews = products.sumOf { it.viewCount }
        val totalSales = products.sumOf { it.soldCount }
        val totalRevenue = products.sumOf { it.price * it.soldCount }

        binding.tvTotalViews.text = NumberFormat.getNumberInstance(Locale.getDefault()).format(totalViews)
        binding.tvTotalSalesCount.text = NumberFormat.getNumberInstance(Locale.getDefault()).format(totalSales)
        binding.tvRevenueValue.text = getString(R.string.format_lkr_price, totalRevenue)

        // Best Selling Products (top 3)
        val bestSellers = products.sortedByDescending { it.soldCount }.take(3)
        displayBestSellers(bestSellers)
    }

    private fun displayBestSellers(bestSellers: List<Product>) {
        binding.layoutBestSellingItems.removeAllViews()

        for (product in bestSellers) {
            val itemBinding = ItemBestSellingProductBinding.inflate(layoutInflater, binding.layoutBestSellingItems, false)
            itemBinding.tvProductName.text = product.productName
            itemBinding.tvSalesInfo.text = getString(R.string.format_sales_info, product.category, product.soldCount)

            Glide.with(this)
                .load(product.imageUrl)
                .placeholder(R.drawable.ic_craftlanka_logo)
                .into(itemBinding.ivProductImage)

            binding.layoutBestSellingItems.addView(itemBinding.root)
        }
    }

    private fun generatePdfReport(uri: Uri) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        paint.color = Color.BLACK
        paint.textSize = 12f
        canvas.drawText("CraftLanka Store Performance Report", 20f, 40f, paint)

        paint.textSize = 10f
        var yPos = 80f

        val totalViews = currentProducts.sumOf { it.viewCount }
        val totalSales = currentProducts.sumOf { it.soldCount }
        val totalRevenue = currentProducts.sumOf { it.price * it.soldCount }

        canvas.drawText("Summary Statistics (Last 30 Days)", 20f, yPos, paint)
        yPos += 20f
        canvas.drawText("Total Product Views: $totalViews", 30f, yPos, paint)
        yPos += 15f
        canvas.drawText("Total Sales: $totalSales", 30f, yPos, paint)
        yPos += 15f
        canvas.drawText("Total Revenue: LKR ${NumberFormat.getNumberInstance(Locale("en", "LK")).format(totalRevenue)}", 30f, yPos, paint)

        yPos += 40f
        canvas.drawText("Top Selling Products:", 20f, yPos, paint)
        yPos += 20f

        currentProducts.sortedByDescending { it.soldCount }.take(10).forEach { product ->
            if (yPos < 580f) {
                canvas.drawText("${product.productName}: ${product.soldCount} sold", 30f, yPos, paint)
                yPos += 15f
            }
        }

        pdfDocument.finishPage(page)

        try {
            requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
                Toast.makeText(requireContext(), "Report downloaded successfully", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("Analytics", "PDF Error", e)
            Toast.makeText(requireContext(), "Failed to generate report", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }

    private fun setupBottomNavigation() {
        val nav = binding.includeBottomNav
        resetAllNavItems()
        updateNavItemVisuals(nav.ivNavAnalytics, nav.tvNavAnalytics, true)

        nav.navHome.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerHomeFragment(), false)
        }
        nav.navRequests.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerRequestsFragment(), false)
        }
        nav.navProducts.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerProductsFragment(), false)
        }
        nav.navInventory.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerInventoryFragment(), false)
        }
        nav.navProfile.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerProfileFragment(), false)
        }
    }

    private fun resetAllNavItems() {
        val nav = binding.includeBottomNav
        updateNavItemVisuals(nav.ivNavHome, nav.tvNavHome, false)
        updateNavItemVisuals(nav.ivNavRequests, nav.tvNavRequests, false)
        updateNavItemVisuals(nav.ivNavProducts, nav.tvNavProducts, false)
        updateNavItemVisuals(nav.ivNavAnalytics, nav.tvNavAnalytics, false)
        updateNavItemVisuals(nav.ivNavInventory, nav.tvNavInventory, false)
        updateNavItemVisuals(nav.ivNavProfile, nav.tvNavProfile, false)
    }

    private fun updateNavItemVisuals(icon: ImageView, label: TextView, isSelected: Boolean) {
        val context = requireContext()
        val color = if (isSelected) R.color.nav_active_content else R.color.nav_inactive_content
        icon.setColorFilter(ContextCompat.getColor(context, color))
        label.setTextColor(ContextCompat.getColor(context, color))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingVar = null
    }
}
