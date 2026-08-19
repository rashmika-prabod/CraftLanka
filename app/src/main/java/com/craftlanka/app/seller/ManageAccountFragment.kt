package com.craftlanka.app.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.craftlanka.app.MainActivity
import com.craftlanka.app.databinding.FragmentManageAccountBinding

class ManageAccountFragment : Fragment() {

    private var _binding: FragmentManageAccountBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentManageAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnChangePassword.setOnClickListener {
            (activity as? MainActivity)?.navigationManager?.replaceFragment(
                fragment = ChangePasswordFragment(),
                addToBackStack = true,
            )
        }

        binding.btnPaymentMethods.setOnClickListener {
            // (activity as? MainActivity)?.navigationManager?.replaceFragment(
            //     fragment = SellerPaymentFragment(),
            //     addToBackStack = true
            // )
            Toast.makeText(requireContext(), "Payment methods coming soon", Toast.LENGTH_SHORT).show()
        }

        binding.btnDeleteAccount.setOnClickListener {
            val dialog = DeleteAccountDialog()
            dialog.show(parentFragmentManager, "delete_account_dialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
