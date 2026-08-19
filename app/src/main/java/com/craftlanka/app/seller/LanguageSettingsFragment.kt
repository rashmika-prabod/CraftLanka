package com.craftlanka.app.seller

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.craftlanka.app.databinding.FragmentLanguageSettingsBinding

class LanguageSettingsFragment : Fragment() {

    private var _binding: FragmentLanguageSettingsBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLanguageSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val currentLang = prefs.getString("language", "English")

        // Set initial state
        updateSelection(currentLang ?: "English")

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnLangEnglish.setOnClickListener { updateSelection("English") }
        binding.btnLangSinhala.setOnClickListener { updateSelection("Sinhala") }
        binding.btnLangTamil.setOnClickListener { updateSelection("Tamil") }

        binding.btnSave.setOnClickListener {
            val selected = when {
                binding.radioEnglish.isChecked -> "English"
                binding.radioSinhala.isChecked -> "Sinhala"
                binding.radioTamil.isChecked -> "Tamil"
                else -> "English"
            }
            prefs.edit().putString("language", selected).apply()
            Toast.makeText(requireContext(), "Language saved", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun updateSelection(lang: String) {
        binding.radioEnglish.isChecked = lang == "English"
        binding.radioSinhala.isChecked = lang == "Sinhala"
        binding.radioTamil.isChecked = lang == "Tamil"

        // Update card borders
        val context = requireContext()
        val activeColor = androidx.core.content.ContextCompat.getColor(context, com.craftlanka.app.R.color.brand_green)
        val inactiveColor = android.graphics.Color.parseColor("#EAEAEA")

        binding.btnLangEnglish.strokeWidth = if (lang == "English") 4 else 2
        binding.btnLangEnglish.setStrokeColor(if (lang == "English") activeColor else inactiveColor)

        binding.btnLangSinhala.strokeWidth = if (lang == "Sinhala") 4 else 2
        binding.btnLangSinhala.setStrokeColor(if (lang == "Sinhala") activeColor else inactiveColor)

        binding.btnLangTamil.strokeWidth = if (lang == "Tamil") 4 else 2
        binding.btnLangTamil.setStrokeColor(if (lang == "Tamil") activeColor else inactiveColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
