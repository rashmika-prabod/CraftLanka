package com.craftlanka.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.craftlanka.app.databinding.ActivityAddAddressBinding

class AddAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Pre-fill if address already exists
        AddressManager.currentAddress?.let {
            binding.etFullName.setText(it.fullName)
            binding.etPhone.setText(it.phone)
            binding.etAddressLine.setText(it.addressLine)
            binding.etCity.setText(it.city)
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSaveAddress.setOnClickListener {
            val fullName = binding.etFullName.text.toString()
            val phone = binding.etPhone.text.toString()
            val address = binding.etAddressLine.text.toString()
            val city = binding.etCity.text.toString()

            if (fullName.isEmpty() || phone.isEmpty() || address.isEmpty() || city.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Save to AddressManager
                AddressManager.currentAddress = UserAddress(
                    fullName = fullName,
                    phone = phone,
                    addressLine = address,
                    city = city,
                )

                Toast.makeText(this, "Address updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
