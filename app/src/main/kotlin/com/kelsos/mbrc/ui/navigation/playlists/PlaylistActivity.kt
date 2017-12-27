package com.kelsos.mbrc.ui.navigation.playlists

import android.os.Bundle
import androidx.core.view.isGone
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.playlists.Playlist
import com.kelsos.mbrc.databinding.ActivityPlaylistsBinding
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import com.kelsos.mbrc.ui.navigation.playlists.PlaylistAdapter.OnPlaylistPressedListener
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import java.net.ConnectException
import javax.inject.Inject

class PlaylistActivity :
  BaseNavigationActivity(),
  PlaylistView,
  OnPlaylistPressedListener,
  OnRefreshListener {

  @Inject lateinit var adapter: PlaylistAdapter
  @Inject lateinit var presenter: PlaylistPresenter
  private lateinit var scope: Scope
  private lateinit var binding: ActivityPlaylistsBinding

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityPlaylistsBinding.inflate(layoutInflater)
    setContentView(binding.root)
    scope = Toothpick.openScopes(application, PRESENTER_SCOPE, this)
    scope.installTestModules(SmoothieActivityModule(this), PlaylistModule())
    Toothpick.inject(this, scope)

    super.setup()
    adapter.setPlaylistPressedListener(this)
    binding.playlistsPlaylistList.layoutManager = LinearLayoutManager(this)
    binding.playlistsPlaylistList.adapter = adapter
    binding.playlistsRefreshLayout.setOnRefreshListener(this)
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
    if (!binding.playlistsRefreshLayout.isRefreshing) {
      binding.playlistsRefreshLayout.isRefreshing = true
    }

    presenter.reload()
  }

  override suspend fun update(data: PagingData<Playlist>) {
    adapter.submitData(data)
    binding.playlistsEmptyGroup.isGone = adapter.itemCount != 0
    binding.playlistsRefreshLayout.isRefreshing = false
  }

  override fun failure(throwable: Throwable) {
    val swipeLayout = binding.playlistsRefreshLayout
    swipeLayout.isRefreshing = false
    if (throwable.cause is ConnectException) {
      Snackbar.make(swipeLayout, R.string.service_connection_error, Snackbar.LENGTH_SHORT).show()
    } else {
      Snackbar.make(swipeLayout, R.string.playlists_load_failed, Snackbar.LENGTH_SHORT).show()
    }
  }

  override fun showLoading() {
  }

  override fun hideLoading() {
    binding.playlistsEmptyGroup.isGone = true
    binding.playlistsRefreshLayout.isRefreshing = false
  }

  @javax.inject.Scope
  @Target(AnnotationTarget.TYPE)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter

  companion object {
    private val PRESENTER_SCOPE: Class<*> = Presenter::class.java
  }
}
