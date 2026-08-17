package com.craftlanka.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.craftlanka.app.databinding.ActivityOrderDetailsBinding

class OrderDetails : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // In a real app, we would get data from Intent extras
        // For now, we use the design spec's mock data
        binding.tvProductName.text = "Minimalist Ceramic Vase"
        binding.tvProductQty.text = "Qty: 2 • LKR 1,750 each"
        binding.tvItemSubtotal.text = "LKR 3,500.00"

        binding.tvSummarySubtotal.text = "LKR 3,500.00"
        binding.tvSummaryShipping.text = "LKR 350.00"
        binding.tvSummaryTotal.text = "LKR 3,850.00"

        binding.tvCustomerName.text = "Benjamin Carter"
        binding.tvRecipientName.text = "Benjamin Carter"
        binding.tvFullAddress.text = "458 Lotus Lane, Colombo 00700, Sri Lanka"
        binding.tvTrackingNumber.text = "1Z999AA10123456784"
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCopyAddress.setOnClickListener {
            copyToClipboard("Delivery Address", binding.tvFullAddress.text.toString())
        }

        binding.btnUpdateOrder.setOnClickListener {
            val bottomSheet = CancelOrderBottomSheetFragment.newInstance()
            bottomSheet.show(supportFragmentManager, CancelOrderBottomSheetFragment.TAG)
        }

        binding.btnMenu.setOnClickListener {
            Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyToClipboard(label: String, text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Address copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}
