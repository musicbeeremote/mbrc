package com.kelsos.mbrc.features.lyrics.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelsos.mbrc.common.ui.extensions.animateIfEmpty
import com.kelsos.mbrc.databinding.FragmentLyricsBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LyricsFragment : Fragment() {

  private val viewModel: LyricsViewModel by viewModel()
  private val adapter: LyricsAdapter by inject()
  private var _binding: FragmentLyricsBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentLyricsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val layoutManager = LinearLayoutManager(requireContext())
    binding.lyricsLyricsList.setHasFixedSize(true)
    binding.lyricsLyricsList.layoutManager = layoutManager
    binding.lyricsLyricsList.adapter = adapter

    lifecycleScope.launch {
      viewModel.lyrics.collect { lyrics ->
        binding.lyricsLyricsList.animateIfEmpty(adapter.itemCount)
        binding.lyricsEmptyGroup.isGone = lyrics.isNotEmpty()
        adapter.submitList(lyrics)
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
