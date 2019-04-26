package com.kelsos.mbrc.features.playlists.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.FragmentPlaylistsBinding
import com.kelsos.mbrc.features.playlists.presentation.PlaylistAdapter.OnPlaylistPressedListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment(), OnPlaylistPressedListener {

  private val adapter: PlaylistAdapter by inject()
  private val viewModel: PlaylistViewModel by viewModel()

  private var _binding: FragmentPlaylistsBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    adapter.setPlaylistPressedListener(this)
    binding.playlistsPlaylistList.layoutManager = LinearLayoutManager(requireContext())
    binding.playlistsPlaylistList.adapter = adapter
    binding.playlistsRefreshLayout.setOnRefreshListener { viewModel.reload() }

    lifecycleScope.launch {
      adapter.loadStateFlow.drop(1).distinctUntilChangedBy { it.refresh }.collect { state ->
        if (state.refresh is LoadState.NotLoading) {
          binding.playlistsLoadingBar.isGone = true
        }

        binding.playlistsRefreshLayout.isRefreshing = state.refresh is LoadState.Loading
        val isEmpty = state.refresh is LoadState.NotLoading && adapter.itemCount == 0
        binding.playlistsEmptyGroup.isGone = !isEmpty
      }
    }

    lifecycleScope.launch {
      viewModel.playlists.collect { list ->
        adapter.submitData(list)
      }
    }

    lifecycleScope.launch {
      viewModel.emitter.collect { event ->
        val resId = when (event) {
          PlaylistUiMessages.RefreshFailed -> R.string.playlists__refresh_failed
          PlaylistUiMessages.RefreshSuccess -> R.string.playlists__refresh_success
        }
        Snackbar.make(requireView(), resId, Snackbar.LENGTH_SHORT).show()
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun playlistPressed(path: String) {
    viewModel.play(path)
  }
}
