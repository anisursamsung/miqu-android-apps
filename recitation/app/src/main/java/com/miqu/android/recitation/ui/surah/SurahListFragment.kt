package com.miqu.android.recitation.ui.surah

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.miqu.android.recitation.R
import com.miqu.android.recitation.data.SurahRepository
import com.miqu.android.recitation.databinding.FragmentSurahListBinding
import com.miqu.android.recitation.model.Surah
import com.miqu.android.recitation.ui.reader.ReaderActivity

class SurahListFragment : Fragment() {

    private var _binding: FragmentSurahListBinding? = null
    private val binding get() = _binding!!

    private lateinit var surahRepository: SurahRepository
    private lateinit var adapter: SurahAdapter
    private var allSurahs: List<Surah> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurahListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        surahRepository = SurahRepository(requireContext())
        allSurahs = surahRepository.getSurahs()

        adapter = SurahAdapter { surah ->
            val intent = Intent(requireContext(), ReaderActivity::class.java).apply {
                putExtra(ReaderActivity.EXTRA_SURAH_ID, surah.id)
                putExtra(ReaderActivity.EXTRA_SURAH_NAME, surah.name)
                putExtra(ReaderActivity.EXTRA_SURAH_TRANSLITERATION, surah.transliteration)
                putExtra(ReaderActivity.EXTRA_TOTAL_VERSES, surah.totalVerses)
            }
            startActivity(intent)
        }

        binding.recyclerViewSurahs.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSurahs.adapter = adapter
        adapter.submitList(allSurahs)

        binding.searchEditText.doAfterTextChanged {
            filterSurahs()
        }

        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, _ ->
            filterSurahs()
        }
    }

    private fun filterSurahs() {
        val query = binding.searchEditText.text?.toString()?.trim() ?: ""
        val checkedChipId = binding.chipGroupFilter.checkedChipId

        val filtered = allSurahs.filter { surah ->
            val matchesFilter = when (checkedChipId) {
                R.id.chipMeccan -> surah.type.equals("meccan", ignoreCase = true)
                R.id.chipMedinan -> surah.type.equals("medinan", ignoreCase = true)
                else -> true
            }

            val matchesQuery = if (query.isEmpty()) true else {
                surah.id.toString() == query ||
                        surah.transliteration.contains(query, ignoreCase = true) ||
                        surah.name.contains(query) ||
                        surah.english.contains(query, ignoreCase = true) ||
                        surah.bengali.contains(query) ||
                        surah.urdu.contains(query)
            }

            matchesFilter && matchesQuery
        }

        adapter.submitList(filtered)
        binding.textEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
