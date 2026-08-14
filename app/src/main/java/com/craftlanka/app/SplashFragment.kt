package com.craftlanka.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.craftlanka.app.databinding.FragmentSplashBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Run a timer in the background that doesn't block the UI
        lifecycleScope.launch {
            delay(2500) // Display the splash screen animation for 2.5 seconds

            // Access MainActivity to prepare for navigation
            val mainActivity = requireActivity() as MainActivity

            // TODO: In the next step, we will check user role and navigate
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}