package com.miqu.android.recitation.ui.reader

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.miqu.android.recitation.data.UserSettings
import com.miqu.android.recitation.databinding.BottomSheetReaderAppearanceBinding
import com.miqu.android.recitation.ui.settings.SettingsActivity
import com.miqu.android.recitation.util.FontHelper

class ReaderAppearanceBottomSheetFragment(
    private val onAppearanceChanged: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetReaderAppearanceBinding? = null
    private val binding get() = _binding!!

    private lateinit var settings: UserSettings

    private val translationOptions = listOf(
        "English (Sahih International)" to UserSettings.TRANS_ENGLISH,
        "Bengali (মুহিউদ্দীন খান / ইসলামিক ফাউন্ডেশন)" to UserSettings.TRANS_BENGALI,
        "Urdu (احمد علی)" to UserSettings.TRANS_URDU,
        "Indonesian (Bahasa Indonesia)" to UserSettings.TRANS_INDONESIAN,
        "Assamese (অসমীয়া)" to UserSettings.TRANS_ASSAMESE,
        "English (Abdullah Yusuf Ali)" to UserSettings.TRANS_YUSUF_ALI
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetReaderAppearanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settings = UserSettings(requireContext())

        // Font size slider
        binding.sliderFontSize.value = settings.arabicFontSize
        binding.textFontSizeLabel.text = "Arabic Font Size (${settings.arabicFontSize.toInt()}sp)"

        binding.sliderFontSize.addOnChangeListener { _, value, _ ->
            settings.arabicFontSize = value
            binding.textFontSizeLabel.text = "Arabic Font Size (${value.toInt()}sp)"
            onAppearanceChanged()
        }

        // Translation font size slider
        binding.sliderTranslationFontSize.value = settings.translationFontSize
        binding.textTranslationFontSizeLabel.text = "Translation Font Size (${settings.translationFontSize.toInt()}sp)"

        binding.sliderTranslationFontSize.addOnChangeListener { _, value, _ ->
            settings.translationFontSize = value
            binding.textTranslationFontSizeLabel.text = "Translation Font Size (${value.toInt()}sp)"
            onAppearanceChanged()
        }

        // Translation dropdown
        val transDisplayList = translationOptions.map { it.first }
        val transAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, transDisplayList)
        binding.dropdownTranslation.setAdapter(transAdapter)
        val currentTransIndex = translationOptions.indexOfFirst { it.second == settings.translation }.coerceAtLeast(0)
        binding.dropdownTranslation.setText(transDisplayList[currentTransIndex], false)
        binding.dropdownTranslation.setOnItemClickListener { _, _, position, _ ->
            settings.translation = translationOptions[position].second
            setupTranslationFontDropdown()
            onAppearanceChanged()
        }

        // Translation Font dropdown
        setupTranslationFontDropdown()

        // More settings button
        binding.btnAllSettings.setOnClickListener {
            dismiss()
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupTranslationFontDropdown() {
        val fontList = FontHelper.getFontsForLanguage(settings.translation)
        val displayList = fontList.map { it.second }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, displayList)
        binding.dropdownTranslationFont.setAdapter(adapter)

        val currentSelectedKey = FontHelper.getSelectedFontForLanguage(settings, settings.translation)
        val selectedIndex = fontList.indexOfFirst { it.first == currentSelectedKey }.coerceAtLeast(0)
        binding.dropdownTranslationFont.setText(displayList[selectedIndex], false)

        binding.dropdownTranslationFont.setOnItemClickListener { _, _, position, _ ->
            FontHelper.setSelectedFontForLanguage(settings, settings.translation, fontList[position].first)
            onAppearanceChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
