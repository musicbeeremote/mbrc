package com.kelsos.mbrc.ui.navigation.playlists

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.playlists.Playlist
import com.kelsos.mbrc.databinding.ActivityPlaylistsBinding
import com.kelsos.mbrc.databinding.ListEmptyViewBinding
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import com.kelsos.mbrc.ui.navigation.playlists.PlaylistAdapter.OnPlaylistPressedListener
import com.raizlabs.android.dbflow.list.FlowCursorList
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
  private lateinit var emptyBinding: ListEmptyViewBinding

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityPlaylistsBinding.inflate(layoutInflater)
    emptyBinding = ListEmptyViewBinding.bind(binding.root)
    setContentView(binding.root)
    scope = Toothpick.openScopes(application, PRESENTER_SCOPE, this)
    scope.installTestModules(SmoothieActivityModule(this), PlaylistModule())
    Toothpick.inject(this, scope)

    super.setup()

    binding.swipeLayout.setSwipeableChildren(R.id.playlist_list, R.id.empty_view)
    adapter.setPlaylistPressedListener(this)
    binding.playlistList.layoutManager = LinearLayoutManager(this)
    binding.playlistList.emptyView = emptyBinding.emptyView
    binding.playlistList.adapter = adapter
    binding.swipeLayout.setOnRefreshListener(this)
    emptyBinding.listEmptyTitle.setText(R.string.playlists_list_empty)
    presenter.attach(this)
    presenter.load()
  }

  override fun playlistPressed(path: String) {
    presenter.play(path)
  }

  override fun active(): Int {
    return R.id.nav_playlists
  }

  override fun onDestroy() {
    presenter.detach()
    Toothpick.closeScope(this)

    if (isFinishing) {
      Toothpick.closeScope(PRESENTER_SCOPE)
    }
    super.onDestroy()
  }

  override fun onRefresh() {
    if (!binding.swipeLayout.isRefreshing) {
      binding.swipeLayout.isRefreshing = true
    }

    presenter.reload()
  }

  override fun update(cursor: FlowCursorList<Playlist>) {
    adapter.update(cursor)
    binding.swipeLayout.isRefreshing = false
  }

  override fun failure(throwable: Throwable) {
    val swipeLayout = binding.swipeLayout
    swipeLayout.isRefreshing = false
    if (throwable.cause is ConnectException) {
      Snackbar.make(swipeLayout, R.string.service_connection_error, Snackbar.LENGTH_SHORT).show()
    } else {
      Snackbar.make(swipeLayout, R.string.playlists_load_failed, Snackbar.LENGTH_SHORT).show()
    }
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

  @javax.inject.Scope
  @Target(AnnotationTarget.TYPE)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter

  companion object {
    private val PRESENTER_SCOPE: Class<*> = Presenter::class.java
  }
}
