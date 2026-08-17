package com.craftlanka.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.craftlanka.app.databinding.ActivityCheckoutBinding
import com.craftlanka.app.databinding.ItemCheckoutProductBinding
import java.util.Locale

class Checkout : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private var subtotalAmount = 0.0
    private var deliveryFeeAmount = 450.0 // Default to Express

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        populateOrderSummary()
        setupClickListeners()
        setupPaymentSelection()
        setupDeliverySelection()
        updateAddressDisplay()
        updateCardDisplay()
    }

    override fun onResume() {
        super.onResume()
        updateAddressDisplay()
        updateCardDisplay()
    }

    private fun updateAddressDisplay() {
        AddressManager.currentAddress?.let {
            binding.tvAddressName.text = it.fullName
            binding.tvAddressText.text = it.addressLine
            binding.tvAddressPhone.text = it.phone
            binding.rbAddress.isChecked = true
        } ?: run {
            binding.tvAddressName.text = "No address selected"
            binding.tvAddressText.text = "Please add a delivery address"
            binding.tvAddressPhone.text = ""
            binding.rbAddress.isChecked = false
        }
    }

    private fun updateCardDisplay() {
        PaymentManager.currentCard?.let {
            val last4 = it.cardNumber.takeLast(4)
            binding.tvVisaCardInfo.text = "Visa/Mastercard ending in $last4"
        }
    }

    private fun populateOrderSummary() {
        val cartItems = CartManager.items
        binding.llCheckoutItems.removeAllViews()

        if (cartItems.isEmpty()) {
            finish()
            return
        }

        subtotalAmount = 0.0

        for (product in cartItems) {
            val itemBinding = ItemCheckoutProductBinding.inflate(LayoutInflater.from(this), binding.llCheckoutItems, false)
            itemBinding.tvProductName.text = product.title
            itemBinding.tvProductDetails.text = "Artisan: ${product.artisan} | ${product.category}"
            itemBinding.tvProductPrice.text = product.priceString

            binding.llCheckoutItems.addView(itemBinding.root)
            subtotalAmount += product.priceAmount
        }

        updatePriceBreakdown()
    }

    private fun updatePriceBreakdown() {
        val giftWrapping = 150.0
        val promoDiscount = subtotalAmount * 0.2

        val total = subtotalAmount + deliveryFeeAmount + giftWrapping - promoDiscount

        binding.tvSubtotalLabel.text = getString(R.string.subtotal_items, CartManager.items.size)
        binding.tvSubtotalValue.text = String.format(Locale.getDefault(), "LKR %,.0f", subtotalAmount)
        binding.tvDeliveryFeeValue.text = String.format(Locale.getDefault(), "LKR %,.0f", deliveryFeeAmount)
        binding.tvTotalValue.text = String.format(Locale.getDefault(), "LKR %,.0f", total)
        binding.tvBottomTotal.text = String.format(Locale.getDefault(), "LKR %,.0f", total)
    }

    private fun setupClickListeners() {
        // Back Button
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Edit Cart - leads back to cart page
        binding.btnEditCart.setOnClickListener {
            val intent = Intent(this, Cart::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        // Add New Address / Update Address
        binding.btnAddAddress.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            startActivity(intent)
        }

        binding.cardAddress.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            startActivity(intent)
        }

        // Add Payment Card
        binding.btnAddCard.setOnClickListener {
            val intent = Intent(this, AddCardActivity::class.java)
            startActivity(intent)
        }

        // Place Order Button
        binding.btnPlaceOrder.setOnClickListener {
            if (AddressManager.currentAddress == null) {
                Toast.makeText(this, "Please add a delivery address first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.rbVisa.isChecked && PaymentManager.currentCard == null) {
                Toast.makeText(this, "Please add your card details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val paymentMethod = if (binding.rbVisa.isChecked) "Visa/Mastercard" else "Cash on delivery"
            Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupDeliverySelection() {
        binding.btnExpress.setOnClickListener {
            deliveryFeeAmount = 450.0
            binding.btnExpress.setBackgroundResource(R.drawable.bg_card_selected)
            binding.btnRegular.setBackgroundResource(R.drawable.bg_card_white)
            updatePriceBreakdown()
        }

        binding.btnRegular.setOnClickListener {
            deliveryFeeAmount = 150.0
            binding.btnRegular.setBackgroundResource(R.drawable.bg_card_selected)
            binding.btnExpress.setBackgroundResource(R.drawable.bg_card_white)
            updatePriceBreakdown()
        }
    }

    private fun setupPaymentSelection() {
        val selectVisa = {
            binding.rbVisa.isChecked = true
            binding.rbCash.isChecked = false
            binding.containerVisa.setBackgroundResource(R.drawable.bg_card_selected)
            binding.containerCash.setBackgroundResource(R.drawable.bg_card_white)
        }
        val selectCash = {
            binding.rbCash.isChecked = true
            binding.rbVisa.isChecked = false
            binding.containerCash.setBackgroundResource(R.drawable.bg_card_selected)
            binding.containerVisa.setBackgroundResource(R.drawable.bg_card_white)
        }

        binding.containerVisa.setOnClickListener { selectVisa() }
        binding.containerCash.setOnClickListener { selectCash() }
        binding.rbVisa.setOnClickListener { selectVisa() }
        binding.rbCash.setOnClickListener { selectCash() }
    }
}
