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
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.playlists.Playlist
import com.kelsos.mbrc.databinding.FragmentPlaylistsBinding
import com.kelsos.mbrc.ui.navigation.playlists.PlaylistAdapter.OnPlaylistPressedListener
import org.koin.android.ext.android.inject
import java.net.ConnectException

class PlaylistFragment :
  Fragment(),
  PlaylistView,
  OnPlaylistPressedListener,
  OnRefreshListener {

  private val adapter: PlaylistAdapter by inject()
  private val presenter: PlaylistPresenter by inject()

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
    presenter.attach(this)
    presenter.load()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun playlistPressed(path: String) {
    presenter.play(path)
  }

  override fun onDestroy() {
    presenter.detach()
    super.onDestroy()
  }

  override fun onRefresh() {
    if (!binding.playlistsRefreshLayout.isRefreshing) {
      binding.playlistsRefreshLayout.isRefreshing = true
    }

    presenter.reload()
  }

  override suspend fun update(data: PagingData<Playlist>) {
    adapter.submitData(data)
    binding.playlistsEmptyGroup.isGone = adapter.itemCount != 0
    binding.playlistsRefreshLayout.isRefreshing = false
  }

  override fun failure(throwable: Throwable) {
    val swipeLayout = binding.playlistsRefreshLayout
    swipeLayout.isRefreshing = false
    val resId = if (throwable.cause is ConnectException) {
      R.string.service_connection_error
    } else {
      R.string.playlists_load_failed
    }
    Snackbar.make(requireView(), resId, Snackbar.LENGTH_SHORT).show()
  }

  override fun showLoading() {
  }

  override fun hideLoading() {
    binding.playlistsEmptyGroup.isGone = true
    binding.playlistsRefreshLayout.isRefreshing = false
  }
}
