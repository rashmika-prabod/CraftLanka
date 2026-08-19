package com.craftlanka.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.graphics.toColorInt
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.craftlanka.app.databinding.FragmentLanguageSelectionBinding
import com.google.android.material.card.MaterialCardView

class LanguageSelectionFragment : Fragment() {
    private var bindingVar: FragmentLanguageSelectionBinding? = null
    private val binding get() = bindingVar!!

    private var selectedLanguageCode = "en"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentLanguageSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Read current language from preferences to highlight the correct card
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        selectedLanguageCode = prefs.getString("selected_language", "en") ?: "en"
        selectLanguage(selectedLanguageCode)

        binding.cardEnglish.setOnClickListener { selectLanguage("en") }
        binding.cardSinhala.setOnClickListener { selectLanguage("si") }
        binding.cardTamil.setOnClickListener { selectLanguage("ta") }

        binding.btnSkip.setOnClickListener {
            saveLanguageAndProceed(selectedLanguageCode)
        }

        binding.btnContinue.setOnClickListener {
            saveLanguageAndProceed(selectedLanguageCode)
        }
    }

    private fun selectLanguage(langCode: String) {
        selectedLanguageCode = langCode

        // Update visual selection only
        updateCardStyle(binding.cardEnglish, binding.ivIconEnglish, langCode == "en")
        updateCardStyle(binding.cardSinhala, binding.ivIconSinhala, langCode == "si")
        updateCardStyle(binding.cardTamil, binding.ivIconTamil, langCode == "ta")
    }

    private fun updateCardStyle(
        card: MaterialCardView,
        icon: ImageView,
        isSelected: Boolean,
    ) {
        if (isSelected) {
            val selectedColor = "#0E3818".toColorInt()
            card.strokeColor = selectedColor
            card.strokeWidth = dpToPx(2)
            icon.setColorFilter(selectedColor)
        } else {
            val unselectedCardColor = "#D0D0D0".toColorInt()
            val unselectedIconColor = "#666666".toColorInt()
            card.strokeColor = unselectedCardColor
            card.strokeWidth = dpToPx(1)
            icon.setColorFilter(unselectedIconColor)
        }
    }

    private fun saveLanguageAndProceed(langCode: String) {
        // 1. Save to preferences
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit {
            putString("selected_language", langCode)
        }

        // 2. Apply globally (This triggers an automatic Activity recreation)
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(langCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingVar = null
    }
}
