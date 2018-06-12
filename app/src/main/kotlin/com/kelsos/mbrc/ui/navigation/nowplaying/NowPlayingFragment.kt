package com.kelsos.mbrc.ui.navigation.nowplaying

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.paging.PagingData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel
import com.kelsos.mbrc.content.nowplaying.NowPlaying
import com.kelsos.mbrc.databinding.FragmentNowplayingBinding
import com.kelsos.mbrc.ui.drag.OnStartDragListener
import com.kelsos.mbrc.ui.drag.SimpleItemTouchHelper
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class NowPlayingFragment :
  Fragment(),
  NowPlayingView,
  OnQueryTextListener,
  OnStartDragListener,
  NowPlayingAdapter.NowPlayingListener {

  private var _binding: FragmentNowplayingBinding? = null
  private val binding get() = _binding!!

  private val adapter: NowPlayingAdapter by lazy { NowPlayingAdapter(this@NowPlayingFragment) }

  @Inject
  lateinit var presenter: NowPlayingPresenter
  private var searchView: SearchView? = null
  private var searchMenuItem: MenuItem? = null
  private lateinit var scope: Scope
  private lateinit var touchListener: NowPlayingTouchListener
  private var itemTouchHelper: ItemTouchHelper? = null

  override fun onQueryTextSubmit(query: String): Boolean {
    closeSearch()
    presenter.search(query)
    return true
  }

  private fun closeSearch(): Boolean {
    searchView?.let {
      if (it.isShown) {
        it.isIconified = true
        it.isFocusable = false
        it.clearFocus()
        it.setQuery("", false)
        searchMenuItem?.collapseActionView()
        return true
      }
    }
    return false
  }

  override fun onQueryTextChange(newText: String): Boolean {
    return true
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.nowplaying_search, menu)
    searchMenuItem = menu.findItem(R.id.now_playing_search)?.apply {
      searchView = actionView as SearchView
    }

    searchView?.apply {
      queryHint = getString(R.string.now_playing_search_hint)
      setIconifiedByDefault(true)
      setOnQueryTextListener(this@NowPlayingFragment)
    }

    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(requireActivity().application, this)
    scope.installModules(NowPlayingModule.create())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentNowplayingBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val manager = LinearLayoutManager(requireContext())
    binding.nowPlayingTrackList.layoutManager = manager
    binding.nowPlayingTrackList.adapter = adapter
    binding.nowPlayingTrackList.itemAnimator?.changeDuration = 0
    touchListener = NowPlayingTouchListener(requireContext()) {
      if (it) {
        binding.nowPlayingRefreshLayout.isRefreshing = false
        binding.nowPlayingRefreshLayout.isEnabled = false
        binding.nowPlayingRefreshLayout.cancelPendingInputEvents()
      } else {
        binding.nowPlayingRefreshLayout.isEnabled = true
      }
    }
    binding.nowPlayingTrackList.addOnItemTouchListener(touchListener)
    val callback = SimpleItemTouchHelper(adapter)
    itemTouchHelper = ItemTouchHelper(callback)
    itemTouchHelper!!.attachToRecyclerView(binding.nowPlayingTrackList)
    adapter.setListener(this)
    binding.nowPlayingRefreshLayout.setOnRefreshListener { this.refresh() }
    presenter.attach(this)
    presenter.load()
    refresh(true)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  private fun refresh(scrollToTrack: Boolean = false) {
    presenter.reload(scrollToTrack)
  }

  override fun onPress(position: Int) {
    presenter.play(position + 1)
  }

  override fun onMove(from: Int, to: Int) {
    presenter.moveTrack(from, to)
  }

  override fun onDismiss(position: Int) {
    presenter.removeTrack(position)
  }

  override fun onDestroy() {
    presenter.detach()
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override suspend fun update(data: PagingData<NowPlaying>) {
    adapter.submitData(data)
    binding.nowPlayingEmptyGroup.isGone = adapter.itemCount != 0
    binding.nowPlayingRefreshLayout.isRefreshing = false
  }

  override fun trackChanged(track: PlayingTrackModel, scrollToTrack: Boolean) {
    adapter.setPlayingTrack(track.path)
    if (scrollToTrack) {
      binding.nowPlayingTrackList.scrollToPosition(adapter.getPlayingTrackIndex())
    }
  }

  override fun failure(throwable: Throwable) {
    binding.nowPlayingRefreshLayout.isRefreshing = false
    Snackbar.make(binding.root, R.string.refresh_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun loading(show: Boolean) {
    if (!show) {
      binding.nowPlayingLoadingBar.isGone = true
      binding.nowPlayingRefreshLayout.isRefreshing = false
    }
  }

  override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
    itemTouchHelper?.startDrag(viewHolder)
  }
}
