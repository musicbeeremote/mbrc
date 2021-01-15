package com.kelsos.mbrc.features.lyrics.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.extensions.animateIfEmpty
import com.kelsos.mbrc.databinding.FragmentLyricsBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LyricsFragment : Fragment() {
  private val viewModel: LyricsViewModel by viewModel()
  private val lyricsAdapter: LyricsAdapter by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentLyricsBinding = DataBindingUtil.inflate(
      inflater,
      R.layout.fragment_lyrics,
      container,
      false
    )

    binding.lyricsLyricsList.apply {
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(requireContext())
      adapter = lyricsAdapter
    }

    viewModel.lyrics.observe(this) { lyrics ->
      binding.lyricsLyricsList.animateIfEmpty(lyricsAdapter.itemCount)
      binding.lyricsEmptyGroup.isVisible = lyrics.isEmpty()
      lyricsAdapter.submitList(lyrics)
    }

    return binding.root
  }
}
