package com.craftlanka.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.craftlanka.app.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {
    private var bindingVar: FragmentOnboardingBinding? = null
    private val binding get() = bindingVar!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Handle Get Started button click -> navigate to Language Selection
        binding.btnGetStarted.setOnClickListener {
            navigateToLanguageSelection()
        }

        // Handle Skip button click -> navigate to Language Selection
        binding.btnSkip.setOnClickListener {
            navigateToLanguageSelection()
        }
    }

    private fun navigateToLanguageSelection() {
        val mainActivity = requireActivity() as MainActivity

        // Route to LanguageSelectionFragment
        mainActivity.navigationManager.replaceFragment(
            fragment = LanguageSelectionFragment(),
            addToBackStack = false,
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingVar = null
    }
}
