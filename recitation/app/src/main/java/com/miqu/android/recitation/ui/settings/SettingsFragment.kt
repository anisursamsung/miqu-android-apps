package com.miqu.android.recitation.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.miqu.android.recitation.R
import com.miqu.android.recitation.data.TafsirRepository
import com.miqu.android.recitation.data.UserSettings
import com.miqu.android.recitation.databinding.FragmentSettingsBinding
import com.miqu.android.recitation.util.FontHelper

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
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
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settings = UserSettings(requireContext())

        // Arabic Font dropdown setup
        val arabicFontList = FontHelper.ARABIC_FONTS
        val arabicDisplayList = arabicFontList.map { it.second }
        val arabicAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arabicDisplayList)
        binding.dropdownFontArabic.setAdapter(arabicAdapter)
        val currentArabicIndex = arabicFontList.indexOfFirst { it.first == settings.fontArabic }.coerceAtLeast(0)
        binding.dropdownFontArabic.setText(arabicDisplayList[currentArabicIndex], false)
        binding.dropdownFontArabic.setOnItemClickListener { _, _, position, _ ->
            settings.fontArabic = arabicFontList[position].first
            updatePreview()
        }

        // Translation dropdown setup
        val transDisplayList = translationOptions.map { it.first }
        val transAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, transDisplayList)
        binding.dropdownTranslation.setAdapter(transAdapter)

        val currentTransIndex = translationOptions.indexOfFirst { it.second == settings.translation }
            .coerceAtLeast(0)
        binding.dropdownTranslation.setText(transDisplayList[currentTransIndex], false)

        binding.dropdownTranslation.setOnItemClickListener { _, _, position, _ ->
            settings.translation = translationOptions[position].second
            setupTranslationFontDropdown()
            updatePreview()
        }

        // Translation Font dropdown setup
        setupTranslationFontDropdown()

        // Tafsir dropdown setup
        val tafsirList = TafsirRepository.AVAILABLE_TAFSIRS
        val tafsirDisplayList = tafsirList.map { it.displayName }
        val tafsirAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, tafsirDisplayList)
        binding.dropdownTafsir.setAdapter(tafsirAdapter)

        val currentTafsirIndex = tafsirList.indexOfFirst { it.fileName == settings.tafsir }
            .coerceAtLeast(0)
        binding.dropdownTafsir.setText(tafsirDisplayList[currentTafsirIndex], false)

        binding.dropdownTafsir.setOnItemClickListener { _, _, position, _ ->
            settings.tafsir = tafsirList[position].fileName
        }

        // Arabic font size slider setup
        binding.sliderArabicFontSize.value = settings.arabicFontSize
        binding.textFontSizeLabel.text = "Arabic Font Size (${settings.arabicFontSize.toInt()}sp)"

        binding.sliderArabicFontSize.addOnChangeListener { _, value, _ ->
            settings.arabicFontSize = value
            binding.textFontSizeLabel.text = "Arabic Font Size (${value.toInt()}sp)"
            updatePreview()
        }

        // Translation font size slider setup
        binding.sliderTranslationFontSize.value = settings.translationFontSize
        binding.textTranslationFontSizeLabel.text = "Translation Font Size (${settings.translationFontSize.toInt()}sp)"

        binding.sliderTranslationFontSize.addOnChangeListener { _, value, _ ->
            settings.translationFontSize = value
            binding.textTranslationFontSizeLabel.text = "Translation Font Size (${value.toInt()}sp)"
            updatePreview()
        }

        updatePreview()
    }

    private val sampleTranslations = mapOf(
        UserSettings.TRANS_ENGLISH to "In the name of Allah, the Entirely Merciful, the Especially Merciful",
        UserSettings.TRANS_BENGALI to "শুরু করছি আল্লাহর নামে যিনি পরম করুণাময়, অতি দয়ালু।",
        UserSettings.TRANS_URDU to "اللہ کے نام سے جو رحمان و رحیم ہے",
        UserSettings.TRANS_INDONESIAN to "Dengan nama Allah Yang Maha Pengasih, Maha Penyayang",
        UserSettings.TRANS_ASSAMESE to "(আৰম্ভ কৰিছোঁ) পৰম কৰুণাময় পৰম দয়ালু আল্লাহৰ নামত।",
        UserSettings.TRANS_YUSUF_ALI to "In the name of Allah, Most Gracious, Most Merciful."
    )

    private fun setupTranslationFontDropdown() {
        val fontList = FontHelper.getFontsForLanguage(settings.translation)
        val displayList = fontList.map { it.second }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, displayList)
        binding.dropdownFontTranslation.setAdapter(adapter)

        val currentSelectedKey = FontHelper.getSelectedFontForLanguage(settings, settings.translation)
        val selectedIndex = fontList.indexOfFirst { it.first == currentSelectedKey }.coerceAtLeast(0)
        binding.dropdownFontTranslation.setText(displayList[selectedIndex], false)

        binding.dropdownFontTranslation.setOnItemClickListener { _, _, position, _ ->
            FontHelper.setSelectedFontForLanguage(settings, settings.translation, fontList[position].first)
            updatePreview()
        }
    }

    private fun updatePreview() {
        binding.textArabicPreview.typeface = FontHelper.getArabicTypeface(requireContext())
        binding.textArabicPreview.textSize = settings.arabicFontSize

        binding.textTranslationPreview.typeface = FontHelper.getTranslationTypeface(requireContext(), settings)
        binding.textTranslationPreview.textSize = settings.translationFontSize
        binding.textTranslationPreview.text = sampleTranslations[settings.translation]
            ?: sampleTranslations[UserSettings.TRANS_ENGLISH]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
