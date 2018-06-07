package com.kelsos.mbrc.ui.navigation.playlists

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.Group
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.playlists.Playlist
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import com.kelsos.mbrc.ui.navigation.playlists.PlaylistAdapter.OnPlaylistPressedListener
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import java.net.ConnectException
import javax.inject.Inject

class PlaylistActivity : BaseNavigationActivity(),
  PlaylistView,
  OnPlaylistPressedListener,
  OnRefreshListener {

  private val swipeLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout by bindView(R.id.playlists__refresh_layout)
  private val playlistList: androidx.recyclerview.widget.RecyclerView by bindView(R.id.playlists__playlist_list)
  private val emptyView: Group by bindView(R.id.playlists__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.playlists__text_title)
  private val emptyViewProgress: ProgressBar by bindView(R.id.playlists__loading_bar)

  @Inject
  lateinit var adapter: PlaylistAdapter
  @Inject
  lateinit var presenter: PlaylistPresenter

  private lateinit var scope: Scope

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, PRESENTER_SCOPE, this)
    scope.installTestModules(SmoothieActivityModule(this), PlaylistModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_playlists)
    super.setup()

    adapter.setPlaylistPressedListener(this)
    playlistList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
    playlistList.adapter = adapter
    swipeLayout.setOnRefreshListener(this)
    emptyViewTitle.setText(R.string.playlists_list_empty)
    presenter.attach(this)
    presenter.load()
  }

  override fun playlistPressed(path: String) {
    presenter.play(path)
  }

  override fun active(): Int = R.id.nav_playlists

  override fun onDestroy() {
    presenter.detach()
    Toothpick.closeScope(this)

    if (isFinishing) {
      Toothpick.closeScope(PRESENTER_SCOPE)
    }
    super.onDestroy()
  }

  override fun onRefresh() {
    if (!swipeLayout.isRefreshing) {
      swipeLayout.isRefreshing = true
    }

    presenter.reload()
  }

  override fun update(cursor: List<Playlist>) {
    emptyView.isVisible = cursor.isEmpty()
    adapter.update(cursor)
    swipeLayout.isRefreshing = false
  }

  override fun failure(throwable: Throwable) {
    swipeLayout.isRefreshing = false
    if (throwable.cause is ConnectException) {
      com.google.android.material.snackbar.Snackbar.make(swipeLayout, R.string.service_connection_error, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
    } else {
      com.google.android.material.snackbar.Snackbar.make(swipeLayout, R.string.playlists_load_failed, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
    }
  }

  override fun showLoading() {
  }

  override fun hideLoading() {
    emptyViewProgress.isVisible = false
    swipeLayout.isRefreshing = false
  }

  @javax.inject.Scope
  @Target(AnnotationTarget.TYPE)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter

  companion object {
    private val PRESENTER_SCOPE: Class<*> = Presenter::class.java
  }
}