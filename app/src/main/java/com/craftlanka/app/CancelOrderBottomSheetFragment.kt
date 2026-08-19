package com.craftlanka.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.craftlanka.app.databinding.BottomSheetCancelOrderBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CancelOrderBottomSheetFragment : BottomSheetDialogFragment() {

    @Suppress("ktlint:standard:property-naming")
    private var _binding: BottomSheetCancelOrderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetCancelOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRadioGroup()
        setupRefundMethodDropdown()
        setupCharCounter()
        setupClickListeners()
    }

    private fun setupRadioGroup() {
        binding.rgReasons.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.rbOther.id) {
                binding.llOtherDetail.visibility = View.VISIBLE
            } else {
                binding.llOtherDetail.visibility = View.GONE
            }
        }
    }

    private fun setupRefundMethodDropdown() {
        val methods = arrayOf("Manual bank transfer", "Original payment method", "Store credit")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, methods)
        binding.actvRefundMethod.setAdapter(adapter)
    }

    private fun setupCharCounter() {
        binding.etOtherReason.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                binding.tvCharCounter.text = "$length/200"
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListeners() {
        binding.btnConfirmCancel.setOnClickListener {
            if (validateInput()) {
                Toast.makeText(context, "Order Cancelled Successfully", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        binding.btnKeepOrder.setOnClickListener {
            dismiss()
        }
    }

    private fun validateInput(): Boolean {
        if (binding.rgReasons.checkedRadioButtonId == -1) {
            Toast.makeText(context, "Please select a reason", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.rgReasons.checkedRadioButtonId == binding.rbOther.id &&
            binding.etOtherReason.text.isNullOrBlank()
        ) {
            Toast.makeText(context, "Please specify the reason", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etAccountHolder.text.isNullOrBlank()) {
            binding.tilAccountHolder.error = "Required"
            return false
        } else {
            binding.tilAccountHolder.error = null
        }

        if (!binding.cbUnderstand.isChecked) {
            Toast.makeText(context, "Please confirm that you understand", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CancelOrderBottomSheet"
        fun newInstance() = CancelOrderBottomSheetFragment()
    }
}
