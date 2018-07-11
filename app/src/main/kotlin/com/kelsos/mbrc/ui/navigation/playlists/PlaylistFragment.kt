package com.kelsos.mbrc.ui.navigation.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.kelsos.mbrc.content.playlists.Playlist
import com.kelsos.mbrc.databinding.FragmentPlaylistsBinding
import com.kelsos.mbrc.ui.navigation.playlists.PlaylistAdapter.OnPlaylistPressedListener
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment :
  Fragment(),
  OnPlaylistPressedListener,
  OnRefreshListener {

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
    binding.playlistsRefreshLayout.setOnRefreshListener(this)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun playlistPressed(path: String) {
    viewModel.play(path)
  }

  override fun onRefresh() {
    if (!binding.playlistsRefreshLayout.isRefreshing) {
      binding.playlistsRefreshLayout.isRefreshing = true
    }

    viewModel.reload()
  }

  suspend fun update(data: PagingData<Playlist>) {
    adapter.submitData(data)
    binding.playlistsEmptyGroup.isGone = adapter.itemCount != 0
    binding.playlistsRefreshLayout.isRefreshing = false
  }
}
