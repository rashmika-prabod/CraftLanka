package com.craftlanka.app.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.craftlanka.app.MainActivity
import com.craftlanka.app.R
import com.craftlanka.app.data.AuthRepository
import com.craftlanka.app.data.SellerRepository
import com.craftlanka.app.databinding.FragmentSellerRequestsBinding
import com.craftlanka.app.databinding.LayoutRejectRequestModalBinding
import com.craftlanka.app.model.BuyerRequest
import com.craftlanka.app.model.Product
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class SellerRequestsFragment : Fragment() {

    private var bindingVar: FragmentSellerRequestsBinding? = null
    private val binding get() = bindingVar!!

    private lateinit var requestsAdapter: SellerRequestsAdapter
    private val authRepository = AuthRepository()
    private val sellerRepository = SellerRepository()

    private val currentUid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private var allRequests = listOf<BuyerRequest>()
    private var isAutoAcceptEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentSellerRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupTabs()
        setupBottomNavigation()
        loadSellerProfile()
        fetchRequests()
        setupAutoAcceptToggle()

        binding.btnProfileHeader.setOnClickListener {
            Toast.makeText(requireContext(), "Profile feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        requestsAdapter = SellerRequestsAdapter(
            requests = emptyList(),
            onAccept = { request -> handleManualAccept(request) },
            onReject = { request -> showRejectionModal(request) },
        )
        binding.rvRequests.adapter = requestsAdapter
    }

    private fun setupTabs() {
        binding.tabLayoutRequests.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                filterRequestsByTab(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadSellerProfile() {
        val uid = currentUid
        if (uid.isEmpty()) return

        authRepository.getSellerProfile(uid) { profile ->
            if (isAdded && profile != null) {
                isAutoAcceptEnabled = profile.autoAcceptRequests
                binding.switchAutoAccept.isChecked = isAutoAcceptEnabled

                if (profile.photoUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(profile.photoUrl)
                        .circleCrop()
                        .into(binding.ivHeaderProfile)
                }
            }
        }
    }

    private fun fetchRequests() {
        val uid = currentUid
        if (uid.isEmpty()) return

        sellerRepository.getSellerRequests(
            uid,
            onSuccess = { requests ->
                if (isAdded) {
                    allRequests = requests.sortedBy { it.timestamp }
                    if (isAutoAcceptEnabled) {
                        processAutoAccept()
                    } else {
                        filterRequestsByTab(binding.tabLayoutRequests.selectedTabPosition)
                    }
                }
            },
            onFailure = { error ->
                if (isAdded) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            },
        )
    }

    private fun setupAutoAcceptToggle() {
        binding.switchAutoAccept.setOnCheckedChangeListener { _, isChecked ->
            isAutoAcceptEnabled = isChecked
            sellerRepository.updateAutoAcceptPreference(
                currentUid,
                isChecked,
                onSuccess = {
                    if (isAdded && isChecked) {
                        processAutoAccept()
                    }
                },
                onFailure = { error ->
                    if (isAdded) Toast.makeText(requireContext(), "Failed: $error", Toast.LENGTH_SHORT).show()
                },
            )
        }
    }

    private fun processAutoAccept() {
        val pendingRequests = allRequests.filter { it.status == "PENDING" }
        if (pendingRequests.isEmpty()) {
            filterRequestsByTab(binding.tabLayoutRequests.selectedTabPosition)
            return
        }

        sellerRepository.getSellerProducts(
            currentUid,
            onSuccess = { products ->
                if (isAdded) {
                    autoProcessRequestsSequentially(pendingRequests, products)
                }
            },
            onFailure = { filterRequestsByTab(binding.tabLayoutRequests.selectedTabPosition) },
        )
    }

    private fun autoProcessRequestsSequentially(pending: List<BuyerRequest>, products: List<Product>) {
        val stockMap = products.associate { it.productId to it.stockQuantity }.toMutableMap()
        var processedCount = 0
        val totalToProcess = pending.size

        for (request in pending) {
            val availableStock = stockMap[request.productId] ?: 0
            if (availableStock >= request.quantity) {
                sellerRepository.acceptRequest(
                    request,
                    onSuccess = {
                        stockMap[request.productId] = availableStock - request.quantity
                        checkProcessingComplete(++processedCount, totalToProcess)
                    },
                    onFailure = { checkProcessingComplete(++processedCount, totalToProcess) },
                )
            } else {
                sellerRepository.rejectRequest(
                    request.requestId,
                    "Stock unavailable",
                    onSuccess = { checkProcessingComplete(++processedCount, totalToProcess) },
                    onFailure = { checkProcessingComplete(++processedCount, totalToProcess) },
                )
            }
        }
    }

    private fun checkProcessingComplete(processed: Int, total: Int) {
        if (processed == total) {
            fetchRequests()
        }
    }

    private fun handleManualAccept(request: BuyerRequest) {
        sellerRepository.acceptRequest(
            request,
            onSuccess = {
                Toast.makeText(requireContext(), "Request accepted", Toast.LENGTH_SHORT).show()
                fetchRequests()
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), "Failed to accept: $error", Toast.LENGTH_SHORT).show()
            },
        )
    }

    private fun showRejectionModal(request: BuyerRequest) {
        val modalBinding = LayoutRejectRequestModalBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.Theme_CraftLanka_Dialog_Transparent)
            .setView(modalBinding.root)
            .create()

        modalBinding.btnCancelRejection.setOnClickListener { dialog.dismiss() }
        modalBinding.btnConfirmRejection.setOnClickListener {
            val selectedId = modalBinding.rgRejectionReasons.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(requireContext(), "Please select a reason", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reason = when (selectedId) {
                R.id.rb_stock_unavailable -> "Stock unavailable"
                R.id.rb_delivery_restricted -> "Delivery zone restricted"
                R.id.rb_customization_impossible -> "Customization not possible"
                else -> "Other reason"
            }

            sellerRepository.rejectRequest(
                request.requestId,
                reason,
                onSuccess = {
                    dialog.dismiss()
                    fetchRequests()
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "Failed to reject: $error", Toast.LENGTH_SHORT).show()
                },
            )
        }
        dialog.show()
    }

    private fun filterRequestsByTab(position: Int) {
        val filtered = when (position) {
            0 -> allRequests.filter { it.status == "PENDING" }
            1 -> allRequests.filter { it.status == "ACCEPTED" }
            2 -> allRequests.filter { it.status == "REJECTED" }
            else -> allRequests
        }

        requestsAdapter.updateData(filtered)

        binding.tvListTitle.text = when (position) {
            0 -> getString(R.string.title_active_requests)
            1 -> getString(R.string.title_confirmed_orders)
            else -> getString(R.string.title_rejected_requests)
        }

        binding.tvListSubtitle.visibility = if (position == 1) View.VISIBLE else View.GONE
        binding.tvListSubtitle.text = getString(R.string.subtitle_confirmed_orders)

        binding.layoutAcceptedFooter.visibility = if (position == 1 && filtered.isNotEmpty()) View.VISIBLE else View.GONE
        if (position == 1) {
            binding.tvAcceptedCountMessage.text = getString(R.string.format_accepted_requests_weekly, filtered.size)
        }

        binding.cardAutoAccept.visibility = if (position == 0) View.VISIBLE else View.GONE
        binding.layoutEmptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvRequests.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun setupBottomNavigation() {
        val nav = binding.includeBottomNav
        resetAllNavItems()
        updateNavItemVisuals(nav.ivNavRequests, nav.tvNavRequests, true)

        nav.navHome.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerHomeFragment(), false)
        }
        nav.navRequests.setOnClickListener {
            // Already here
        }
        nav.navProducts.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerProductsFragment(), false)
        }
        nav.navAnalytics.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerAnalyticsFragment(), false)
        }
        nav.navInventory.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(SellerInventoryFragment(), false)
        }
        nav.navProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Profile coming soon", Toast.LENGTH_SHORT).show()
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
