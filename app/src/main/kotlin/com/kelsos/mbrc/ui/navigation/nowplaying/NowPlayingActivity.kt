package com.kelsos.mbrc.ui.navigation.nowplaying

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.TrackInfo
import com.kelsos.mbrc.content.nowplaying.NowPlaying
import com.kelsos.mbrc.databinding.ActivityNowplayingBinding
import com.kelsos.mbrc.databinding.ListEmptyViewBinding
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import com.kelsos.mbrc.ui.drag.OnStartDragListener
import com.kelsos.mbrc.ui.drag.SimpleItemTouchHelper
import com.kelsos.mbrc.ui.navigation.nowplaying.NowPlayingAdapter.NowPlayingListener
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class NowPlayingActivity :
  BaseNavigationActivity(),
  NowPlayingView,
  OnQueryTextListener,
  OnStartDragListener,
  NowPlayingListener {

  private lateinit var binding: ActivityNowplayingBinding
  private lateinit var emptyBinding: ListEmptyViewBinding

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
    emptyBinding = ListEmptyViewBinding.bind(binding.root)
    setContentView(binding.root)

    Toothpick.inject(this, scope)
    super.setup()

    binding.swipeLayout.setSwipeableChildren(R.id.now_playing_list, R.id.empty_view)
    binding.nowPlayingList.emptyView = emptyBinding.emptyView
    val manager = LinearLayoutManager(this)
    binding.nowPlayingList.layoutManager = manager
    binding.nowPlayingList.adapter = adapter
    binding.nowPlayingList.itemAnimator?.changeDuration = 0
    touchListener = NowPlayingTouchListener(this) {
      if (it) {
        binding.swipeLayout.clearSwipeableChildren()
        binding.swipeLayout.isRefreshing = false
        binding.swipeLayout.isEnabled = false
        binding.swipeLayout.cancelPendingInputEvents()
      } else {
        binding.swipeLayout.setSwipeableChildren(R.id.now_playing_list, R.id.empty_view)
        binding.swipeLayout.isEnabled = true
      }
    }
    binding.nowPlayingList.addOnItemTouchListener(touchListener)
    val callback = SimpleItemTouchHelper(adapter)
    itemTouchHelper = ItemTouchHelper(callback)
    itemTouchHelper!!.attachToRecyclerView(binding.nowPlayingList)
    adapter.setListener(this)
    binding.swipeLayout.setOnRefreshListener { this.refresh() }
    presenter.attach(this)
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

  override fun active(): Int {
    return R.id.nav_now_playing
  }

  override fun onDestroy() {
    presenter.detach()
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun update(cursor: List<NowPlaying>) {
    adapter.update(cursor)
    binding.swipeLayout.isRefreshing = false
  }

  override fun trackChanged(trackInfo: TrackInfo, scrollToTrack: Boolean) {
    adapter.setPlayingTrack(trackInfo.path)
    if (scrollToTrack) {
      binding.nowPlayingList.scrollToPosition(adapter.getPlayingTrackIndex())
    }
  }

  override fun failure(throwable: Throwable) {
    binding.swipeLayout.isRefreshing = false
    Snackbar.make(binding.nowPlayingList, R.string.refresh_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun showLoading() {
    emptyBinding.emptyViewProgressBar.visibility = View.VISIBLE
    emptyBinding.listEmptyIcon.visibility = View.GONE
    emptyBinding.listEmptyTitle.visibility = View.GONE
    emptyBinding.listEmptySubtitle.visibility = View.GONE
  }

  override fun hideLoading() {
    emptyBinding.emptyViewProgressBar.visibility = View.GONE
    emptyBinding.listEmptyIcon.visibility = View.VISIBLE
    emptyBinding.listEmptyTitle.visibility = View.VISIBLE
    emptyBinding.listEmptySubtitle.visibility = View.VISIBLE
    binding.swipeLayout.isRefreshing = false
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
