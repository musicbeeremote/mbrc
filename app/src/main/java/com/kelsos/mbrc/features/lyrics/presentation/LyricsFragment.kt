package com.kelsos.mbrc.features.lyrics.presentation

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
import com.kelsos.mbrc.common.ui.extensions.animateIfEmpty
import com.kelsos.mbrc.features.minicontrol.MiniControlFactory
import kotterknife.bindView
import org.koin.android.ext.android.inject

class LyricsFragment : Fragment() {

  private val recycler: RecyclerView by bindView(R.id.lyrics__lyrics_list)
  private val emptyView: Group by bindView(R.id.lyrics__empty_group)

  private val viewModel: LyricsViewModel by inject()
  private val lyricsAdapter: LyricsAdapter by inject()
  private val miniControlFactory: MiniControlFactory by inject()

  private fun setupRecycler() {
    recycler.apply {
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

    viewModel.lyrics.observe(this) { lyrics ->
      recycler.animateIfEmpty(lyricsAdapter.itemCount)
      emptyView.isVisible = lyrics.isEmpty()
      lyricsAdapter.submitList(lyrics)
    }

    miniControlFactory.attach(parentFragmentManager)
  }
}