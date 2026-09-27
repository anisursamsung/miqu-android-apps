package com.miqu.android.recitation.data

import android.content.Context
import android.content.SharedPreferences

class UserSettings(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("recitation_prefs", Context.MODE_PRIVATE)

    companion object {
        const val PREF_SCRIPT = "pref_script"
        const val PREF_TRANSLATION = "pref_translation"
        const val PREF_TAFSIR = "pref_tafsir"
        const val PREF_ARABIC_SIZE = "pref_arabic_size"
        const val PREF_TRANSLATION_SIZE = "pref_translation_size"
        const val PREF_SHOW_TRANSLATION = "pref_show_translation"
        const val PREF_FONT_ARABIC = "pref_font_arabic"
        const val PREF_FONT_ENGLISH = "pref_font_english"
        const val PREF_FONT_BENGALI = "pref_font_bengali"

        const val FONT_UTHMAN = "uthman"
        const val FONT_DEFAULT = "default"
        const val FONT_RAISIN_TOPPING = "raisin_topping"
        const val FONT_BANGLA1 = "bangla1"

        const val SCRIPT_UTHMANI = "uthmani"
        const val SCRIPT_INDOPAK = "indopak"

        const val TRANS_ENGLISH = "english"
        const val TRANS_BENGALI = "bengali"
        const val TRANS_URDU = "urdu"
        const val TRANS_INDONESIAN = "indonesian"
        const val TRANS_ASSAMESE = "assamese"
        const val TRANS_YUSUF_ALI = "yusufali"

        const val TAFSIR_EN_IBN_KATHIR = "tafsir_en_ibne_katheer.md"
        const val TAFSIR_BEN_IBN_KATHIR = "tafsir_ben_ibne_katheer.md"
        const val TAFSIR_UR_IBN_KATHIR = "tafsir_ur_ibne_katheer.md"
        const val TAFSIR_EN_MAARIF = "tafsir_en_maarif_ul_quran.md"
        const val TAFSIR_INDO_JALALAYN = "tafsir_indo_jalalayn_tanzil.md"
        const val TAFSIR_AS_MOKHTASAR = "tafsir_as_mokhtasar_islamhouse.md"
    }

    var script: String
        get() = prefs.getString(PREF_SCRIPT, SCRIPT_UTHMANI) ?: SCRIPT_UTHMANI
        set(value) = prefs.edit().putString(PREF_SCRIPT, value).apply()

    var translation: String
        get() = prefs.getString(PREF_TRANSLATION, TRANS_ENGLISH) ?: TRANS_ENGLISH
        set(value) = prefs.edit().putString(PREF_TRANSLATION, value).apply()

    var tafsir: String
        get() = prefs.getString(PREF_TAFSIR, TAFSIR_EN_IBN_KATHIR) ?: TAFSIR_EN_IBN_KATHIR
        set(value) = prefs.edit().putString(PREF_TAFSIR, value).apply()

    var arabicFontSize: Float
        get() = prefs.getFloat(PREF_ARABIC_SIZE, 26f)
        set(value) = prefs.edit().putFloat(PREF_ARABIC_SIZE, value).apply()

    var translationFontSize: Float
        get() = prefs.getFloat(PREF_TRANSLATION_SIZE, 15f)
        set(value) = prefs.edit().putFloat(PREF_TRANSLATION_SIZE, value).apply()

    var showTranslation: Boolean
        get() = prefs.getBoolean(PREF_SHOW_TRANSLATION, true)
        set(value) = prefs.edit().putBoolean(PREF_SHOW_TRANSLATION, value).apply()

    var fontArabic: String
        get() = prefs.getString(PREF_FONT_ARABIC, FONT_UTHMAN) ?: FONT_UTHMAN
        set(value) = prefs.edit().putString(PREF_FONT_ARABIC, value).apply()

    var fontEnglish: String
        get() = prefs.getString(PREF_FONT_ENGLISH, FONT_DEFAULT) ?: FONT_DEFAULT
        set(value) = prefs.edit().putString(PREF_FONT_ENGLISH, value).apply()

    var fontBengali: String
        get() = prefs.getString(PREF_FONT_BENGALI, FONT_DEFAULT) ?: FONT_DEFAULT
        set(value) = prefs.edit().putString(PREF_FONT_BENGALI, value).apply()
}
