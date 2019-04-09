package com.kelsos.mbrc.ui.navigation.lyrics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import kotterknife.bindView
import org.koin.android.ext.android.inject

class LyricsFragment : Fragment() {

  private val lyricsRecycler: RecyclerView by bindView(R.id.lyrics__lyrics_list)
  private val emptyView: Group by bindView(R.id.lyrics__empty_group)

  private val viewModel: LyricsViewModel by inject()
  private val lyricsAdapter: LyricsAdapter by lazy { LyricsAdapter() }

  private fun setupRecycler() {
    lyricsRecycler.apply {
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(requireContext())
      adapter = lyricsAdapter
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_lyrics, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupRecycler()

    viewModel.lyricsLiveDataProvider.observe(this) { lyrics ->
      emptyView.isVisible = lyrics.isEmpty()
      lyricsAdapter.submitList(lyrics)
    }
  }
}