package com.miqu.android.recitation.ui.videos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.miqu.android.recitation.data.VideoRepository
import com.miqu.android.recitation.databinding.FragmentVideosBinding

class VideosFragment : Fragment() {

    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!

    private lateinit var videoRepository: VideoRepository
    private lateinit var adapter: VideoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoRepository = VideoRepository(requireContext())
        adapter = VideoAdapter { video ->
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=${video.id}")
            )
            startActivity(webIntent)
        }

        binding.recyclerViewVideos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewVideos.adapter = adapter
        adapter.submitList(videoRepository.getVideos())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
