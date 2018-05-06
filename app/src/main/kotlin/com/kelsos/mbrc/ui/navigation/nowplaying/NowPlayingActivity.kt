package com.kelsos.mbrc.ui.navigation.nowplaying

import android.arch.paging.PagedList
import android.os.Bundle
import android.support.constraint.Group
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.SearchView.OnQueryTextListener
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel
import com.kelsos.mbrc.content.nowplaying.NowPlayingEntity
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import com.kelsos.mbrc.ui.drag.OnStartDragListener
import com.kelsos.mbrc.ui.drag.SimpleItemTouchHelper
import com.kelsos.mbrc.ui.navigation.nowplaying.NowPlayingAdapter.NowPlayingListener
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class NowPlayingActivity : BaseNavigationActivity(),
  NowPlayingView,
  OnQueryTextListener,
  OnStartDragListener,
  NowPlayingListener {

  private val nowPlayingList: RecyclerView by bindView(R.id.now_playing__track_list)
  private val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.now_playing__refresh_layout)
  private val emptyGroup: Group by bindView(R.id.now_playing__empty_group)
  private val emptyViewProgress: ProgressBar by bindView(R.id.now_playing__loading_bar)
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
    searchMenuItem = menu.findItem(R.id.now_playing_search).apply {
      searchView = actionView as SearchView
    }

    searchView?.apply {
      queryHint = getString(R.string.now_playing_search_hint)
      setIconifiedByDefault(true)
      setOnQueryTextListener(this@NowPlayingActivity)
    }

    return super.onCreateOptionsMenu(menu)
  }

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this), NowPlayingModule.create())
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_nowplaying)

    Toothpick.inject(this, scope)
    super.setup()

    val manager = LinearLayoutManager(this)
    nowPlayingList.layoutManager = manager
    nowPlayingList.adapter = adapter
    nowPlayingList.itemAnimator.changeDuration = 0
    touchListener = NowPlayingTouchListener(this, {
      if (it) {
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.isEnabled = false
        swipeRefreshLayout.cancelPendingInputEvents()
      } else {
        swipeRefreshLayout.isEnabled = true
      }
    })
    nowPlayingList.addOnItemTouchListener(touchListener)
    val callback = SimpleItemTouchHelper(adapter)
    itemTouchHelper = ItemTouchHelper(callback)
    itemTouchHelper!!.attachToRecyclerView(nowPlayingList)
    adapter.setListener(this)
    swipeRefreshLayout.setOnRefreshListener { this.refresh() }
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

  override fun update(data: PagedList<NowPlayingEntity>) {
    emptyGroup.isVisible = data.isEmpty()
    adapter.submitList(data)
    swipeRefreshLayout.isRefreshing = false
  }

  override fun trackChanged(track: PlayingTrackModel, scrollToTrack: Boolean) {
    adapter.setPlayingTrack(track.path)
    if (scrollToTrack) {
      nowPlayingList.scrollToPosition(adapter.getPlayingTrackIndex())
    }
  }

  override fun failure(throwable: Throwable) {
    swipeRefreshLayout.isRefreshing = false
    Snackbar.make(nowPlayingList, R.string.refresh_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun loading(show: Boolean) {
    if (!show) {
      emptyViewProgress.isVisible = false
      swipeRefreshLayout.isRefreshing = false
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