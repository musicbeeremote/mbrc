package com.kelsos.mbrc.ui.activities.nav

import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.NowPlayingAdapter
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.data.NowPlaying
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent
import com.kelsos.mbrc.rx.RxUtils
import com.kelsos.mbrc.services.NowPlayingSync
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.drag.SimpleItenTouchHelper
import rx.schedulers.Schedulers
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import java.util.*
import javax.inject.Inject

class NowPlayingActivity : BaseActivity(), SearchView.OnQueryTextListener, NowPlayingAdapter.NowPlayingListener {

  @BindView(R.id.now_playing_list) lateinit var nowPlayingList: RecyclerView
  @BindView(R.id.swipe_layout) lateinit var swipeRefreshLayout: SwipeRefreshLayout
  @Inject lateinit var bus: RxBus
  @Inject lateinit var adapter: NowPlayingAdapter
  @Inject lateinit var sync: NowPlayingSync
  private val mSearchView: SearchView? = null
  private val mSearchItem: MenuItem? = null
  private var scope: Scope? = null

  private fun handlePlayingTrackChange(event: TrackInfo) {
    if (adapter == null || adapter!!.javaClass != NowPlayingAdapter::class.java) {
      return
    }
    adapter!!.setPlayingTrackIndex(NowPlaying(event.artist, event.title))
    adapter!!.notifyDataSetChanged()
  }

  override fun onQueryTextSubmit(query: String): Boolean {
    bus!!.post(MessageEvent(ProtocolEventType.UserAction,
        UserAction(Protocol.NowPlayingListSearch, query.trim { it <= ' ' })))
    mSearchView!!.isIconified = true
    MenuItemCompat.collapseActionView(mSearchItem)
    return false
  }

  override fun onQueryTextChange(newText: String): Boolean {
    return true
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    //inflater.inflate(R.menu.menu_now_playing, menu);
    //mSearchItem = menu.findItem(R.id.now_playing_search_item);
    //mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
    //mSearchView.setQueryHint(getString(R.string.now_playing_search_hint));
    //mSearchView.setIconifiedByDefault(true);
    //mSearchView.setOnQueryTextListener(this);
    return super.onCreateOptionsMenu(menu)
  }

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_nowplaying)
    ButterKnife.bind(this)
    super.setup()
    val manager = LinearLayoutManager(this)
    nowPlayingList!!.layoutManager = manager
    nowPlayingList!!.adapter = adapter
    nowPlayingList!!.itemAnimator.changeDuration = 0
    val callback = SimpleItenTouchHelper(adapter)
    val helper = ItemTouchHelper(callback)
    helper.attachToRecyclerView(nowPlayingList)
    adapter!!.setListener(this)
    swipeRefreshLayout!!.setOnRefreshListener(OnRefreshListener { this.refresh() })
    refresh()
  }

  private fun refresh() {
    if (!swipeRefreshLayout!!.isRefreshing) {
      swipeRefreshLayout!!.isRefreshing = true
    }

    sync!!.syncNowPlaying(Schedulers.io()).compose(RxUtils.uiTask()).subscribe({
      adapter!!.refresh()
      swipeRefreshLayout!!.isRefreshing = false
    }) { throwable -> Timber.v(throwable, "Failed") }
  }

  public override fun onStart() {
    super.onStart()
  }

  public override fun onResume() {
    super.onResume()
    bus!!.register(this, TrackInfoChangeEvent::class.java,
        { trackInfoChangeEvent -> handlePlayingTrackChange(trackInfoChangeEvent.trackInfo) },
        true)
  }

  public override fun onPause() {
    super.onPause()
    bus!!.unregister(this)
  }

  private fun calculateNewIndex(from: Int, to: Int, index: Int): Int {
    var index = index
    val dist = Math.abs(from - to)
    if (dist == 1 && index == from
        || dist > 1 && from > to && index == from
        || dist > 1 && from < to && index == from) {
      index = to
    } else if (dist == 1 && index == to) {
      index = from
    } else if (dist > 1 && from > to && index == to || from > index && to < index) {
      index += 1
    } else if (dist > 1 && from < to && index == to || from < index && to > index) {
      index -= 1
    }
    return index
  }

  override fun onPress(position: Int) {
    bus!!.post(MessageEvent(ProtocolEventType.UserAction, UserAction(Protocol.NowPlayingListPlay, position + 1)))
  }

  override fun onMove(from: Int, to: Int) {
    adapter!!.setPlayingTrackIndex(calculateNewIndex(from, to, adapter!!.getPlayingTrackIndex()))

    val move = HashMap<String, Int>()
    move.put("from", from)
    move.put("to", to)
    bus!!.post(MessageEvent(ProtocolEventType.UserAction, UserAction(Protocol.NowPlayingListMove, move)))
  }

  override fun onDismiss(position: Int) {
    bus!!.post(MessageEvent(ProtocolEventType.UserAction, UserAction(Protocol.NowPlayingListRemove, position)))
  }

  override fun active(): Int {
    return R.id.nav_now_playing
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }
}
