package com.miqu.android.recitation.ui.reader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.miqu.android.recitation.R
import com.miqu.android.recitation.data.UserSettings
import com.miqu.android.recitation.databinding.ItemVerseBinding
import com.miqu.android.recitation.model.Verse

class VerseAdapter(
    private val context: Context,
    private val userSettings: UserSettings,
    private val onMorphologyClick: (Verse) -> Unit,
    private val onTafsirClick: (Verse) -> Unit
) : RecyclerView.Adapter<VerseAdapter.VerseViewHolder>() {

    private var verses: List<Verse> = emptyList()

    fun submitList(newList: List<Verse>) {
        verses = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerseViewHolder {
        val binding = ItemVerseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VerseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VerseViewHolder, position: Int) {
        holder.bind(verses[position])
    }

    override fun getItemCount(): Int = verses.size

    inner class VerseViewHolder(private val binding: ItemVerseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(verse: Verse) {
            binding.textVerseKey.text = "${verse.surahNumber}:${verse.verseNumber}"
            binding.textArabic.text = verse.arabic
            binding.textArabic.textSize = userSettings.arabicFontSize
            binding.textArabic.typeface = com.miqu.android.recitation.util.FontHelper.getArabicTypeface(context)
            binding.textArabic.textAlignment = android.view.View.TEXT_ALIGNMENT_VIEW_END
            binding.textArabic.gravity = android.view.Gravity.END or android.view.Gravity.RIGHT

            val translationText = verse.getTranslation(userSettings.translation)
            binding.textTranslation.text = translationText
            binding.textTranslation.textSize = userSettings.translationFontSize
            binding.textTranslation.typeface = com.miqu.android.recitation.util.FontHelper.getTranslationTypeface(context, userSettings)

            binding.btnCopyVerse.setOnClickListener {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(
                    "Ayah ${verse.surahNumber}:${verse.verseNumber}",
                    "${verse.arabic}\n\n$translationText\n(${verse.surahNumber}:${verse.verseNumber})"
                )
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, context.getString(R.string.verse_copied), Toast.LENGTH_SHORT).show()
            }

            binding.btnMorphology.setOnClickListener {
                onMorphologyClick(verse)
            }

            binding.btnTafsir.setOnClickListener {
                onTafsirClick(verse)
            }
        }
    }
}
