package com.miqu.android.recitation.ui.lexicon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miqu.android.recitation.databinding.ItemRootOccurrenceBinding
import com.miqu.android.recitation.model.WordRoot

class RootOccurrencesAdapter(
    private val onOccurrenceClick: (WordRoot) -> Unit
) : RecyclerView.Adapter<RootOccurrencesAdapter.OccurrenceViewHolder>() {

    private var occurrences: List<WordRoot> = emptyList()

    fun submitList(newList: List<WordRoot>) {
        occurrences = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OccurrenceViewHolder {
        val binding =
            ItemRootOccurrenceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OccurrenceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OccurrenceViewHolder, position: Int) {
        holder.bind(occurrences[position])
    }

    override fun getItemCount(): Int = occurrences.size

    inner class OccurrenceViewHolder(private val binding: ItemRootOccurrenceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wordRoot: WordRoot) {
            binding.textOccurrenceSurahVerse.text = "Ayah ${wordRoot.surah}:${wordRoot.verse}"
            binding.textOccurrenceArabic.text = wordRoot.arabic
            binding.textOccurrenceArabic.typeface = com.miqu.android.recitation.util.FontHelper.getArabicTypeface(binding.root.context)
            binding.textOccurrenceEnglish.text = wordRoot.english

            binding.root.setOnClickListener {
                onOccurrenceClick(wordRoot)
            }
        }
    }
}
