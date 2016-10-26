package com.kelsos.mbrc.ui.navigation.nowplaying

import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.NowPlayingAdapter
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.nowplaying.NowPlayingModule
import com.kelsos.mbrc.ui.navigation.nowplaying.NowPlayingPresenter
import com.kelsos.mbrc.ui.navigation.nowplaying.NowPlayingView
import com.kelsos.mbrc.ui.drag.SimpleItenTouchHelper
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class NowPlayingActivity : BaseActivity(),
    NowPlayingView,
    SearchView.OnQueryTextListener,
    NowPlayingAdapter.NowPlayingListener {

  @BindView(R.id.now_playing_list) lateinit var nowPlayingList: EmptyRecyclerView
  @BindView(R.id.swipe_layout) lateinit var swipeRefreshLayout: MultiSwipeRefreshLayout
  @BindView(R.id.empty_view) lateinit var emptyView: View
  @Inject lateinit var adapter: NowPlayingAdapter

  @Inject lateinit var presenter: NowPlayingPresenter
  private var searchView: SearchView? = null
  private var searchItem: MenuItem? = null
  private var scope: Scope? = null

  override fun onQueryTextSubmit(query: String): Boolean {
    bus.post(MessageEvent.action(UserAction(Protocol.NowPlayingListSearch, query.trim { it <= ' ' })))
    searchView!!.setQuery("", false)
    searchView!!.isIconified = true
    searchView!!.clearFocus()
    MenuItemCompat.collapseActionView(searchItem)
    return true
  }

  override fun onQueryTextChange(newText: String): Boolean {
    return true
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.nowplaying_search, menu)
    searchItem = menu.findItem(R.id.now_playing_search)
    searchView = MenuItemCompat.getActionView(searchItem) as SearchView
    searchView?.queryHint = getString(R.string.now_playing_search_hint)
    searchView?.setIconifiedByDefault(true)
    searchView?.setOnQueryTextListener(this)
    return super.onCreateOptionsMenu(menu)
  }

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope?.installModules(NowPlayingModule.create())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_nowplaying)
    ButterKnife.bind(this)
    super.setup()
    swipeRefreshLayout.setSwipeableChildren(R.id.now_playing_list, R.id.empty_view)
    nowPlayingList.emptyView = emptyView
    val manager = LinearLayoutManager(this)
    nowPlayingList.layoutManager = manager
    nowPlayingList.adapter = adapter
    nowPlayingList.itemAnimator.changeDuration = 0
    val callback = SimpleItenTouchHelper(adapter)
    val helper = ItemTouchHelper(callback)
    helper.attachToRecyclerView(nowPlayingList)
    adapter.setListener(this)
    swipeRefreshLayout.setOnRefreshListener { this.refresh() }
    presenter.attach(this)
    refresh()
  }

  private fun refresh() {
    if (!swipeRefreshLayout.isRefreshing) {
      swipeRefreshLayout.isRefreshing = true
    }
    presenter.refresh()
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

  override fun refreshingDone() {
    swipeRefreshLayout.isRefreshing = false
  }

  override fun reload() {
    adapter.refresh()
  }

  override fun trackChanged(trackInfo: TrackInfo) {
    adapter.setPlayingTrack(trackInfo.path)
  }
}
