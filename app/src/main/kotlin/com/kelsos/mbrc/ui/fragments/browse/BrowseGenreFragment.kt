package com.kelsos.mbrc.ui.fragments.browse

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.EndlessRecyclerViewScrollListener
import com.kelsos.mbrc.adapters.GenreEntryAdapter
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Genre
import com.kelsos.mbrc.presenters.BrowseGenrePresenter
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment
import com.kelsos.mbrc.ui.fragments.profile.GenreArtistsActivity
import com.kelsos.mbrc.ui.views.BrowseGenreView
import toothpick.Toothpick
import javax.inject.Inject

class BrowseGenreFragment : Fragment(),
    BrowseGenreView,
    GenreEntryAdapter.MenuItemSelectedListener,
    PlaylistDialogFragment.PlaylistActionListener {

  @BindView(R.id.library_recycler) internal lateinit var recyclerView: RecyclerView
  @Inject lateinit var adapter: GenreEntryAdapter
  @Inject lateinit var presenter: BrowseGenrePresenter
  private var scrollListener: EndlessRecyclerViewScrollListener? = null
  private lateinit var scope: toothpick.Scope

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    scope = Toothpick.openScopes(context.applicationContext, this)
    Toothpick.inject(this, scope)
    setHasOptionsMenu(true)
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }

  override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater!!.inflate(R.menu.menu_now_playing, menu)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_library, container, false)
    ButterKnife.bind(this, view)
    presenter.bind(this)
    val layoutManager = LinearLayoutManager(context)
    recyclerView.layoutManager = layoutManager
    recyclerView.adapter = adapter
    scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
      override fun onLoadMore(page: Int, totalItemsCount: Int) {
        presenter.load(page)
      }
    }
    adapter.setMenuItemSelectedListener(this)
    presenter.load()
    return view
  }

  override fun update(data: List<Genre>) {
    adapter.updateData(data)
  }

  override fun showEnqueueFailure() {
    Snackbar.make(recyclerView, R.string.genre_enqueue_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun showEnqueueSuccess() {
    Snackbar.make(recyclerView, R.string.genre_queued, Snackbar.LENGTH_SHORT).show()
  }

  override fun clear() {
    adapter.clear()
  }

  override fun onMenuItemSelected(menuItem: MenuItem, entry: Genre) {
    when (menuItem.itemId) {
      R.id.popup_genre_play -> presenter.queue(entry, Queue.NOW)
      R.id.popup_genre_queue_last -> presenter.queue(entry, Queue.LAST)
      R.id.popup_genre_queue_next -> presenter.queue(entry, Queue.NEXT)
      R.id.popup_genre_playlist -> showPlaylistDialog(entry.id)
      R.id.popup_genre_artists -> openProfile(entry)
      else -> {
      }
    }
  }

  private fun showPlaylistDialog(id: Long) {
    val dialog = PlaylistDialogFragment.newInstance(id)
    dialog.setPlaylistActionListener(this)
    dialog.show(fragmentManager, "dialog")
  }

  private fun openProfile(genre: Genre) {
    val intent = Intent(activity, GenreArtistsActivity::class.java)
    intent.putExtra(GenreArtistsActivity.GENRE_ID, genre.id)
    intent.putExtra(GenreArtistsActivity.GENRE_NAME, genre.name)
    startActivity(intent)
  }

  override fun onItemClicked(genre: Genre) {
    openProfile(genre)
  }

  override fun onResume() {
    super.onResume()
    recyclerView.addOnScrollListener(scrollListener)
  }

  override fun onPause() {
    super.onPause()
    recyclerView.addOnScrollListener(scrollListener)
  }

  override fun onExistingSelected(selectionId: Long, playlistId: Long) {
    presenter.playlistAdd(selectionId, playlistId)
  }

  override fun onNewSelected(selectionId: Long, name: String) {
    presenter.createPlaylist(selectionId, name)
  }

  companion object {

    fun newInstance(): BrowseGenreFragment {
      return BrowseGenreFragment()
    }
  }
}
