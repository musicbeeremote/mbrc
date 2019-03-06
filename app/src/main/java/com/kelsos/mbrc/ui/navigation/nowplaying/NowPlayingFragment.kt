package com.kelsos.mbrc.ui.navigation.nowplaying

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.extensions.snackbar
import com.kelsos.mbrc.ui.drag.OnStartDragListener
import com.kelsos.mbrc.ui.drag.SimpleItemTouchHelper
import com.kelsos.mbrc.ui.navigation.nowplaying.NowPlayingAdapter.NowPlayingListener
import com.kelsos.mbrc.utilities.nonNullObserver
import kotterknife.bindView
import org.koin.android.ext.android.inject

class NowPlayingFragment : Fragment(),
  OnQueryTextListener,
  OnStartDragListener,
  NowPlayingListener {

  private val nowPlayingList: RecyclerView by bindView(R.id.now_playing__track_list)
  private val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.now_playing__refresh_layout)
  private val emptyGroup: Group by bindView(R.id.now_playing__empty_group)
  private val emptyViewProgress: ProgressBar by bindView(R.id.now_playing__loading_bar)

  private val adapter: NowPlayingAdapter by lazy { NowPlayingAdapter(this@NowPlayingFragment) }

  private val viewModel: NowPlayingViewModel by inject()

  private var searchView: SearchView? = null
  private var searchMenuItem: MenuItem? = null
  private lateinit var touchListener: NowPlayingTouchListener
  private var itemTouchHelper: ItemTouchHelper? = null

  override fun onQueryTextSubmit(query: String): Boolean {
    closeSearch()
    viewModel.search(query)
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

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_nowplaying, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val manager = LinearLayoutManager(requireContext())
    nowPlayingList.layoutManager = manager
    nowPlayingList.adapter = adapter
    nowPlayingList.itemAnimator?.changeDuration = 0
    touchListener = NowPlayingTouchListener(requireContext()) {
      if (it) {
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.isEnabled = false
        swipeRefreshLayout.cancelPendingInputEvents()
      } else {
        swipeRefreshLayout.isEnabled = true
      }
    }
    nowPlayingList.addOnItemTouchListener(touchListener)
    val callback = SimpleItemTouchHelper(adapter)
    itemTouchHelper = ItemTouchHelper(callback).apply {
      attachToRecyclerView(nowPlayingList)
    }
    adapter.setListener(this)
    swipeRefreshLayout.setOnRefreshListener { this.refresh() }

    viewModel.playingTrack.observe(this) {
      adapter.setPlayingTrack(it.path)
    }

    viewModel.nowPlayingTracks.nonNullObserver(this) {
      swipeRefreshLayout.isRefreshing = false
      emptyViewProgress.isVisible = false

      emptyGroup.isVisible = it.isEmpty()
      adapter.submitList(it)
    }

    viewModel.events.nonNullObserver(this) {
      it.getContentIfNotHandled()?.let { code ->

        val messageResId = when (code) {
          1 -> R.string.refresh_failed
          else -> R.string.refresh_failed
        }
        snackbar(messageResId)
      }
    }
  }

  private fun refresh() {
    viewModel.refresh()
  }

  override fun onPress(position: Int) {
    viewModel.play(position + 1)
  }

  override fun onMove(from: Int, to: Int) {
    viewModel.moveTrack(from, to)
  }

  override fun onDismiss(position: Int) {
    viewModel.removeTrack(position)
  }

  fun trackChanged(track: PlayingTrack, scrollToTrack: Boolean) {
    adapter.setPlayingTrack(track.path)
    if (scrollToTrack) {
      nowPlayingList.scrollToPosition(adapter.getPlayingTrackIndex())
    }
  }

//  override fun onBackPressed() {
//    if (closeSearch()) {
//      return
//    }
//    super.onBackPressed()
//  }

  override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
    itemTouchHelper?.startDrag(viewHolder)
  }
}