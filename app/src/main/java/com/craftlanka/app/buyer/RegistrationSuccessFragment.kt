package com.craftlanka.app.buyer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.craftlanka.app.MainActivity
import com.craftlanka.app.databinding.FragmentRegistrationSuccessBinding
import com.craftlanka.app.seller.SellerLoginFragment

class RegistrationSuccessFragment : Fragment() {
    private var bindingVar: FragmentRegistrationSuccessBinding? = null
    private val binding get() = bindingVar!!

    companion object {
        private const val ARG_BUTTON_TEXT = "arg_button_text"

        fun newInstance(buttonText: String): RegistrationSuccessFragment {
            val fragment = RegistrationSuccessFragment()
            val args =
                Bundle().apply {
                    putString(ARG_BUTTON_TEXT, buttonText)
                }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentRegistrationSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Set custom button text if passed
        val customButtonText = arguments?.getString(ARG_BUTTON_TEXT)
        if (!customButtonText.isNullOrEmpty()) {
            binding.btnLoginAccount.text = customButtonText
        }

        // Dynamic Navigation based on saved user_role preference
        binding.btnLoginAccount.setOnClickListener {
            val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val userRole = prefs.getString("user_role", "buyer")

            val targetFragment: Fragment =
                if (userRole == "seller") {
                    SellerLoginFragment()
                } else {
                    BuyerLoginFragment()
                }

            val mainActivity = requireActivity() as MainActivity
            mainActivity.navigationManager.replaceFragment(
                fragment = targetFragment,
                addToBackStack = false,
                clearBackStack = true,
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingVar = null
    }
}
