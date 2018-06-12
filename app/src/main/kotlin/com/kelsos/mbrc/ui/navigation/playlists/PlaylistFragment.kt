package com.kelsos.mbrc.ui.navigation.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.playlists.Playlist
import com.kelsos.mbrc.extensions.snackbar
import com.kelsos.mbrc.ui.navigation.playlists.PlaylistAdapter.OnPlaylistPressedListener
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import java.net.ConnectException
import javax.inject.Inject

class PlaylistFragment : Fragment(),
  PlaylistView,
  OnPlaylistPressedListener,
  OnRefreshListener {

  private val swipeLayout: SwipeRefreshLayout by bindView(R.id.playlists__refresh_layout)
  private val playlistList: RecyclerView by bindView(R.id.playlists__playlist_list)
  private val emptyView: Group by bindView(R.id.playlists__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.playlists__text_title)
  private val emptyViewProgress: ProgressBar by bindView(R.id.playlists__loading_bar)

  @Inject
  lateinit var adapter: PlaylistAdapter

  @Inject
  lateinit var presenter: PlaylistPresenter

  private lateinit var scope: Scope

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_playlists, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    adapter.setPlaylistPressedListener(this)
    playlistList.layoutManager = LinearLayoutManager(requireContext())
    playlistList.adapter = adapter
    swipeLayout.setOnRefreshListener(this)
    emptyViewTitle.setText(R.string.playlists_list_empty)
    presenter.attach(this)
    presenter.load()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(requireActivity().application, PRESENTER_SCOPE, this)
    scope.installTestModules(PlaylistModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun playlistPressed(path: String) {
    presenter.play(path)
  }

  override fun onDestroy() {
    presenter.detach()
    Toothpick.closeScope(this)
    Toothpick.closeScope(PRESENTER_SCOPE)
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
      snackbar(R.string.service_connection_error)
    } else {
      snackbar(R.string.playlists_load_failed)
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