package com.craftlanka.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.craftlanka.app.databinding.ActivityAddCardBinding

class AddCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupTextWatchers()

        // Pre-fill if card already exists
        PaymentManager.currentCard?.let {
            binding.etHolderName.setText(it.holderName)
            binding.etCardNumber.setText(it.cardNumber)
            binding.etExpiryDate.setText(it.expiryDate)
            binding.etCVV.setText(it.cvv)

            updateCardPreview(it.holderName, it.cardNumber, it.expiryDate)
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSaveCard.setOnClickListener {
            val holderName = binding.etHolderName.text.toString()
            val cardNumber = binding.etCardNumber.text.toString()
            val expiryDate = binding.etExpiryDate.text.toString()
            val cvv = binding.etCVV.text.toString()

            if (holderName.isEmpty() || cardNumber.length < 16 || expiryDate.length < 5 || cvv.length < 3) {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            } else {
                PaymentManager.currentCard = PaymentCard(
                    holderName = holderName,
                    cardNumber = cardNumber,
                    expiryDate = expiryDate,
                    cvv = cvv,
                )
                Toast.makeText(this, "Card details saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupTextWatchers() {
        binding.etHolderName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvPreviewHolderName.text = if (s.isNullOrEmpty()) "YOUR NAME" else s.toString().uppercase()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val formatted = if (s.isNullOrEmpty()) {
                    "**** **** **** ****"
                } else {
                    s.toString().chunked(4).joinToString(" ")
                }
                binding.tvPreviewCardNumber.text = formatted
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etExpiryDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvPreviewExpiry.text = if (s.isNullOrEmpty()) "MM/YY" else s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateCardPreview(name: String, number: String, expiry: String) {
        binding.tvPreviewHolderName.text = name.uppercase()
        binding.tvPreviewCardNumber.text = number.chunked(4).joinToString(" ")
        binding.tvPreviewExpiry.text = expiry
    }
}
