package com.kelsos.mbrc.ui.activities.nav

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.PlaylistAdapter
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.ui.PlaylistAvailable
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class PlaylistActivity : BaseActivity(), PlaylistAdapter.OnPlaylistPressedListener, SwipeRefreshLayout.OnRefreshListener {

  @BindView(R.id.swipe_layout) lateinit var swipeLayout: MultiSwipeRefreshLayout
  @BindView(R.id.playlist_list) lateinit var playlistList: EmptyRecyclerView
  @BindView(R.id.empty_view) lateinit var emptyView: View

  @Inject lateinit var adapter: PlaylistAdapter
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
    playlistList.emptyView = emptyView
    playlistList.adapter = adapter
    playlistList.layoutManager = LinearLayoutManager(this)
    swipeLayout.setOnRefreshListener(this)
  }

  public override fun onStart() {
    super.onStart()
    bus.register(this, PlaylistAvailable::class.java, { this.onPlaylistAvailable(it) }, true)
    bus.post(MessageEvent(ProtocolEventType.UserAction, UserAction(Protocol.PlaylistList, true)))
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
    bus.post(MessageEvent(ProtocolEventType.UserAction, UserAction(Protocol.PlaylistList, true)))
  }
}// Required empty public constructor
