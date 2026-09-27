package com.miqu.android.recitation.ui.reader

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miqu.android.recitation.data.SurahRepository
import com.miqu.android.recitation.databinding.ItemExactWordOccurrenceBinding
import com.miqu.android.recitation.model.WordRoot

class ExactWordOccurrencesAdapter(
    private val surahRepo: SurahRepository,
    private val onOccurrenceClick: (WordRoot) -> Unit
) : RecyclerView.Adapter<ExactWordOccurrencesAdapter.ViewHolder>() {

    private var items: List<WordRoot> = emptyList()

    fun submitList(newList: List<WordRoot>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemExactWordOccurrenceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemExactWordOccurrenceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WordRoot) {
            val surah = surahRepo.getSurahById(item.surah)
            val surahLabel = surah?.transliteration ?: "Surah ${item.surah}"
            binding.textWordSurahVerse.text = "Ayah ${item.surah}:${item.verse} • $surahLabel"
            binding.textWordArabicOccur.text = item.arabic
            binding.textWordArabicOccur.typeface = com.miqu.android.recitation.util.FontHelper.getArabicTypeface(binding.root.context)
            binding.textWordOccurMeaning.text = "Meaning in this context: “${item.english}”"

            binding.root.setOnClickListener {
                onOccurrenceClick(item)
            }
        }
    }
}
