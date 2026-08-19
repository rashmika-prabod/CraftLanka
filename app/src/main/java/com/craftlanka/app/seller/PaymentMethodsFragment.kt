package com.craftlanka.app.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.craftlanka.app.databinding.FragmentPaymentMethodsBinding

class PaymentMethodsFragment : Fragment() {

    private var _binding: FragmentPaymentMethodsBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPaymentMethodsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnAddPayment.setOnClickListener {
            Toast.makeText(requireContext(), "Add payment method coming soon", Toast.LENGTH_SHORT).show()
        }

        // Add click listeners for existing payment methods if needed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
