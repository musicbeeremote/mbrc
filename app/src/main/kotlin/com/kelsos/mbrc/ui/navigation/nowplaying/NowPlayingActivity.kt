package com.kelsos.mbrc.ui.navigation.nowplaying

import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.SearchView.OnQueryTextListener
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.now_playing.NowPlaying
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.drag.OnStartDragListener
import com.kelsos.mbrc.ui.drag.SimpleItemTouchHelper
import com.kelsos.mbrc.ui.navigation.nowplaying.NowPlayingAdapter.NowPlayingListener
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class NowPlayingActivity : BaseActivity(),
    NowPlayingView,
    OnQueryTextListener,
    OnStartDragListener,
    NowPlayingListener {

  @BindView(R.id.now_playing_list) lateinit var nowPlayingList: EmptyRecyclerView
  @BindView(R.id.swipe_layout) lateinit var swipeRefreshLayout: MultiSwipeRefreshLayout
  @BindView(R.id.empty_view) lateinit var emptyView: View
  @BindView(R.id.list_empty_title) lateinit var emptyViewTitle: TextView
  @BindView(R.id.list_empty_icon) lateinit var emptyViewIcon: ImageView
  @BindView(R.id.list_empty_subtitle) lateinit var emptyViewSubTitle: TextView
  @BindView(R.id.empty_view_progress_bar) lateinit var emptyViewProgress: ProgressBar
  @Inject lateinit var adapter: NowPlayingAdapter

  @Inject lateinit var presenter: NowPlayingPresenter
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
        MenuItemCompat.collapseActionView(searchMenuItem)
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
    searchView = MenuItemCompat.getActionView(searchMenuItem) as SearchView
    searchView?.queryHint = getString(R.string.now_playing_search_hint)
    searchView?.setIconifiedByDefault(true)
    searchView?.setOnQueryTextListener(this)
    return super.onCreateOptionsMenu(menu)
  }

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this), NowPlayingModule.create())
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_nowplaying)
    ButterKnife.bind(this)
    Toothpick.inject(this, scope)
    super.setup()
    swipeRefreshLayout.setSwipeableChildren(R.id.now_playing_list, R.id.empty_view)
    nowPlayingList.emptyView = emptyView
    val manager = LinearLayoutManager(this)
    nowPlayingList.layoutManager = manager
    nowPlayingList.adapter = adapter
    nowPlayingList.itemAnimator.changeDuration = 0
    touchListener = NowPlayingTouchListener(this, {
      if (it) {
        swipeRefreshLayout.clearSwipeableChildren()
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.isEnabled = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
          swipeRefreshLayout.cancelPendingInputEvents()
        }
      } else {
        swipeRefreshLayout.setSwipeableChildren(R.id.now_playing_list, R.id.empty_view)
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
    refresh(true)
  }

  private fun refresh(scrollToTrack: Boolean = false) {
    if (!swipeRefreshLayout.isRefreshing) {
      swipeRefreshLayout.isRefreshing = true
    }
    presenter.reload(scrollToTrack)
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
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
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun update(cursor: FlowCursorList<NowPlaying>) {
    adapter.update(cursor)
    swipeRefreshLayout.isRefreshing = false
  }

  override fun reload() {
    adapter.refresh()
  }

  override fun trackChanged(trackInfo: TrackInfo, scrollToTrack: Boolean) {
    adapter.setPlayingTrack(trackInfo.path)
    if (scrollToTrack) {
      nowPlayingList.scrollToPosition(adapter.getPlayingTrackIndex())
    }
  }

  override fun failure(throwable: Throwable) {
    swipeRefreshLayout.isRefreshing = false
    Snackbar.make(nowPlayingList, R.string.refresh_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun showLoading() {
    emptyViewProgress.visibility = View.VISIBLE
    emptyViewIcon.visibility = View.GONE
    emptyViewTitle.visibility = View.GONE
    emptyViewSubTitle.visibility = View.GONE
  }

  override fun hideLoading() {
    emptyViewProgress.visibility = View.GONE
    emptyViewIcon.visibility = View.VISIBLE
    emptyViewTitle.visibility = View.VISIBLE
    emptyViewSubTitle.visibility = View.VISIBLE
    swipeRefreshLayout.isRefreshing = false
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
