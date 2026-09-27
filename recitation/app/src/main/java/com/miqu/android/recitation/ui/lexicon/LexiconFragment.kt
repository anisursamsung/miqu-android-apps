package com.miqu.android.recitation.ui.lexicon

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.miqu.android.recitation.data.LexiconRepository
import com.miqu.android.recitation.databinding.FragmentLexiconBinding
import com.miqu.android.recitation.model.RootEntry
import kotlin.concurrent.thread

class LexiconFragment : Fragment() {

    private var _binding: FragmentLexiconBinding? = null
    private val binding get() = _binding!!

    private lateinit var lexiconRepository: LexiconRepository
    private lateinit var adapter: LexiconAdapter
    private var allRoots: List<RootEntry> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLexiconBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lexiconRepository = LexiconRepository(requireContext())
        adapter = LexiconAdapter { entry ->
            val intent = Intent(requireContext(), RootDetailActivity::class.java).apply {
                putExtra(RootDetailActivity.EXTRA_ROOT, entry.root)
                putExtra(RootDetailActivity.EXTRA_DEFINITION, entry.definition)
            }
            startActivity(intent)
        }

        binding.recyclerViewRoots.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRoots.adapter = adapter

        thread(start = true, name = "lexicon-loader") {
            allRoots = lexiconRepository.getAllRoots()
            activity?.runOnUiThread {
                adapter.submitList(allRoots)
                filterRoots()
            }
        }

        binding.searchRootEditText.doAfterTextChanged {
            filterRoots()
        }
    }

    private fun filterRoots() {
        val query = binding.searchRootEditText.text?.toString()?.trim() ?: ""
        val filtered = if (query.isEmpty()) {
            allRoots
        } else {
            allRoots.filter {
                it.root.contains(query) || it.definition.contains(query, ignoreCase = true)
            }
        }
        adapter.submitList(filtered)
        binding.textLexiconEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
