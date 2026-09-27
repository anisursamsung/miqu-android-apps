package com.miqu.android.recitation.ui.mushaf

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.miqu.android.recitation.R
import com.miqu.android.recitation.databinding.FragmentMushafsBinding
import com.miqu.android.recitation.model.MushafType

class MushafsFragment : Fragment() {

    private var _binding: FragmentMushafsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMushafsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set authentic specimens typography
        try {
            binding.textPreviewKingFahad.typeface = ResourcesCompat.getFont(requireContext(), R.font.uthman)
        } catch (_: Exception) {}

        try {
            binding.textPreviewIndoPak.typeface = ResourcesCompat.getFont(requireContext(), R.font.aqqm)
        } catch (_: Exception) {}

        val openKingFahad = {
            val intent = Intent(requireContext(), MushafActivity::class.java).apply {
                putExtra(MushafActivity.EXTRA_MUSHAF_TYPE, MushafType.KING_FAHAD.id)
            }
            startActivity(intent)
        }

        val openIndoPak = {
            val intent = Intent(requireContext(), MushafActivity::class.java).apply {
                putExtra(MushafActivity.EXTRA_MUSHAF_TYPE, MushafType.INDO_PAK.id)
            }
            startActivity(intent)
        }

        binding.cardKingFahad.setOnClickListener { openKingFahad() }
        binding.cardIndoPak.setOnClickListener { openIndoPak() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
