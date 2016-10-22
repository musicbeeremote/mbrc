package com.kelsos.mbrc.ui.navigation

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import javax.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.PlaylistListAdapter
import com.kelsos.mbrc.adapters.PlaylistListAdapter.OnPlaylistPlayPressedListener
import com.kelsos.mbrc.domain.Playlist
import com.kelsos.mbrc.presenters.PlaylistPresenter
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.views.PlaylistListView
import roboguice.RoboGuice

class PlaylistListActivity : BaseActivity(), PlaylistListView, OnPlaylistPlayPressedListener {

  @BindView(R.id.playlist_recycler) lateinit var recyclerView: RecyclerView
  @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
  @BindView(R.id.drawer_layout) lateinit var drawer: DrawerLayout
  @BindView(R.id.navigation_view) lateinit var navigationView: NavigationView

  @Inject private lateinit var adapter: PlaylistListAdapter
  @Inject private lateinit var presenter: PlaylistPresenter

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    RoboGuice.getInjector(this).injectMembers(this)
    setContentView(R.layout.activity_playlist_list)
    ButterKnife.bind(this)
    initialize(toolbar,drawer,navigationView)
    setCurrentSelection(R.id.drawer_menu_playlist)

    presenter.bind(this)
    adapter.setOnPlaylistPlayPressedListener(this)

    val manager = LinearLayoutManager(baseContext)
    recyclerView.layoutManager = manager
    recyclerView.adapter = adapter
  }

  public override fun onStart() {
    super.onStart()
    presenter.load()
  }

  override fun update(playlists: List<Playlist>) {
    adapter.updateData(playlists)
  }

  override fun playlistPlayPressed(playlist: Playlist, position: Int) {
    if (!playlist.path.isNullOrEmpty()) {
      presenter.play(playlist.path!!)
    }

  }

  override fun onBackPressed() {
    ActivityCompat.finishAfterTransition(this)
  }
}
