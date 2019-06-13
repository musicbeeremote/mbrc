package com.kelsos.mbrc.features.playlists.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.extensions.snackbar
import com.kelsos.mbrc.features.minicontrol.MiniControlFactory
import com.kelsos.mbrc.features.playlists.presentation.PlaylistAdapter.OnPlaylistPressedListener
import com.kelsos.mbrc.utilities.nonNullObserver
import kotterknife.bindView
import org.koin.android.ext.android.inject

class PlaylistFragment : Fragment(),
  OnPlaylistPressedListener {

  private val swipeLayout: SwipeRefreshLayout by bindView(R.id.playlists__refresh_layout)
  private val playlistList: RecyclerView by bindView(R.id.playlists__playlist_list)
  private val emptyView: Group by bindView(R.id.playlists__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.playlists__text_title)
  private val emptyViewProgress: ProgressBar by bindView(R.id.playlists__loading_bar)

  private val adapter: PlaylistAdapter by lazy { PlaylistAdapter() }
  private val viewModel: PlaylistViewModel by inject()
  private val miniControlFactory: MiniControlFactory by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_playlists, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    adapter.setPlaylistPressedListener(this)
    playlistList.layoutManager = LinearLayoutManager(requireContext())
    playlistList.adapter = adapter
    swipeLayout.setOnRefreshListener { viewModel.reload() }
    emptyViewTitle.setText(R.string.playlists_list_empty)
    miniControlFactory.attach(requireFragmentManager())
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    viewModel.playlists.nonNullObserver(this) { list ->
      adapter.submitList(list)
      emptyView.isVisible = list.isEmpty()
      emptyViewProgress.isVisible = false
      swipeLayout.isRefreshing = false
    }

    viewModel.emitter.nonNullObserver(this) { event ->
      if (event.hasBeenHandled) {
        return@nonNullObserver
      }

      val resId = when (event.peekContent()) {
        PlaylistUiMessages.RefreshFailed -> R.string.playlists__refresh_failed
        PlaylistUiMessages.RefreshSuccess -> R.string.playlists__refresh_success
      }
      swipeLayout.isRefreshing = false
      snackbar(resId)
    }
  }

  override fun playlistPressed(path: String) {
    viewModel.play(path)
  }
}