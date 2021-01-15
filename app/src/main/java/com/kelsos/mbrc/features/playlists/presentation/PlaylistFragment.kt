package com.kelsos.mbrc.features.playlists.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.extensions.snackbar
import com.kelsos.mbrc.common.utilities.nonNullObserver
import com.kelsos.mbrc.databinding.FragmentPlaylistsBinding
import com.kelsos.mbrc.features.playlists.presentation.PlaylistAdapter.OnPlaylistPressedListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment :
  Fragment(),
  OnPlaylistPressedListener {

  private val adapter: PlaylistAdapter by lazy { PlaylistAdapter() }
  private val viewModel: PlaylistViewModel by viewModel()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentPlaylistsBinding = DataBindingUtil.inflate(
      inflater,
      R.layout.fragment_playlists,
      container,
      false
    )
    adapter.setPlaylistPressedListener(this)
    val playlistsRefreshLayout = binding.playlistsRefreshLayout
    val recyclerView = binding.playlistsPlaylistList
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
    recyclerView.adapter = adapter
    binding.playlistsTextTitle.setText(R.string.playlists_list_empty)

    playlistsRefreshLayout.setOnRefreshListener { viewModel.reload() }

    viewModel.playlists.nonNullObserver(viewLifecycleOwner) { list ->
      adapter.submitList(list)
      binding.playlistsEmptyGroup.isVisible = list.isEmpty()
      binding.playlistsLoadingBar.isVisible = false
      playlistsRefreshLayout.isRefreshing = false
    }

    viewModel.emitter.nonNullObserver(viewLifecycleOwner) { event ->
      if (event.hasBeenHandled) {
        return@nonNullObserver
      }

      val resId = when (event.peekContent()) {
        PlaylistUiMessages.RefreshFailed -> R.string.playlists__refresh_failed
        PlaylistUiMessages.RefreshSuccess -> R.string.playlists__refresh_success
      }
      playlistsRefreshLayout.isRefreshing = false
      snackbar(resId)
    }

    return binding.root
  }

  override fun playlistPressed(path: String) {
    viewModel.play(path)
  }
}
