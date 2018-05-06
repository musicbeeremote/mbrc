package com.kelsos.mbrc.ui.navigation.nowplaying

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.isGone
import androidx.paging.PagingData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel
import com.kelsos.mbrc.content.nowplaying.NowPlaying
import com.kelsos.mbrc.databinding.ActivityNowplayingBinding
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import com.kelsos.mbrc.ui.drag.OnStartDragListener
import com.kelsos.mbrc.ui.drag.SimpleItemTouchHelper
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class NowPlayingActivity :
  BaseNavigationActivity(),
  NowPlayingView,
  OnQueryTextListener,
  OnStartDragListener,
  NowPlayingAdapter.NowPlayingListener {

  private lateinit var binding: ActivityNowplayingBinding

  @Inject
  lateinit var adapter: NowPlayingAdapter

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

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.nowplaying_search, menu)
    searchMenuItem = menu.findItem(R.id.now_playing_search)
    searchView = searchMenuItem?.actionView as SearchView
    searchView?.queryHint = getString(R.string.now_playing_search_hint)
    searchView?.setIconifiedByDefault(true)
    searchView?.setOnQueryTextListener(this)
    return super.onCreateOptionsMenu(menu)
  }

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this), NowPlayingModule.create())
    super.onCreate(savedInstanceState)
    binding = ActivityNowplayingBinding.inflate(layoutInflater)
    setContentView(binding.root)

    Toothpick.inject(this, scope)
    super.setup()

    val manager = LinearLayoutManager(this)
    binding.nowPlayingTrackList.layoutManager = manager
    binding.nowPlayingTrackList.adapter = adapter
    binding.nowPlayingTrackList.itemAnimator?.changeDuration = 0
    touchListener = NowPlayingTouchListener(this) {
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

  override fun active(): Int = R.id.nav_now_playing

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

  override fun onBackPressed() {
    if (closeSearch()) {
      return
    }
    super.onBackPressed()
  }

  override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
    itemTouchHelper?.startDrag(viewHolder)
  }
}
