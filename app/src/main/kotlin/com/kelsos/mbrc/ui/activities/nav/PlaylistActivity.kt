package com.kelsos.mbrc.ui.activities.nav

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.PlaylistAdapter
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.ui.PlaylistAvailable
import com.kelsos.mbrc.services.PlaylistService
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import java.net.ConnectException
import javax.inject.Inject

class PlaylistActivity : BaseActivity(), PlaylistAdapter.OnPlaylistPressedListener, SwipeRefreshLayout.OnRefreshListener {

  @BindView(R.id.swipe_layout) lateinit var swipeLayout: MultiSwipeRefreshLayout
  @BindView(R.id.playlist_list) lateinit var playlistList: EmptyRecyclerView
  @BindView(R.id.empty_view) lateinit var emptyView: View
  @BindView(R.id.list_empty_title) lateinit var emptyViewTitle: TextView

  @Inject lateinit var adapter: PlaylistAdapter
  @Inject lateinit var service: PlaylistService
  private var scope: Scope? = null

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_playlists)
    ButterKnife.bind(this)
    super.setup()
    swipeLayout.setSwipeableChildren(R.id.playlist_list, R.id.empty_view)
    adapter.setPlaylistPressedListener(this)
    playlistList.layoutManager = LinearLayoutManager(this)
    playlistList.emptyView = emptyView
    playlistList.adapter = adapter
    swipeLayout.setOnRefreshListener(this)
    emptyViewTitle.setText(R.string.playlists_list_empty)
    onRefresh()
  }

  public override fun onStart() {
    super.onStart()
    bus.register(this, PlaylistAvailable::class.java, { this.onPlaylistAvailable(it) }, true)
    bus.post(MessageEvent.action(UserAction(Protocol.PlaylistList, true)))
  }

  public override fun onStop() {
    super.onStop()
    bus.unregister(this)
  }

  private fun onPlaylistAvailable(event: PlaylistAvailable) {
    swipeLayout.isRefreshing = false
    adapter.update(event.playlist)
  }

  override fun playlistPressed(path: String) {
    bus.post(MessageEvent(ProtocolEventType.UserAction, UserAction(Protocol.PlaylistPlay, path)))
  }

  override fun active(): Int {
    return R.id.nav_playlists
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun onRefresh() {
    if (!swipeLayout.isRefreshing) {
      swipeLayout.isRefreshing = true
    }

    service.getPlaylists(0, 5000)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnTerminate { swipeLayout.isRefreshing = false }
        .subscribe({
          adapter.update(it.data)
        }, {
          if (it.cause is ConnectException) {
            Snackbar.make(swipeLayout, R.string.service_connection_error, Snackbar.LENGTH_SHORT).show()
          } else {
            Snackbar.make(swipeLayout, R.string.playlists_load_failed, Snackbar.LENGTH_SHORT).show()
          }

          Timber.v(it, "Failed to load playlists")
        })
  }
}
