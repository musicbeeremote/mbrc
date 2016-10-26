package com.kelsos.mbrc.ui.navigation.library.artists

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.ArtistEntryAdapter
import com.kelsos.mbrc.adapters.EndlessRecyclerViewScrollListener
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.ui.navigation.library.artists.BrowseArtistPresenter
import com.kelsos.mbrc.ui.navigation.library.artist_albums.ArtistAlbumsActivity
import com.kelsos.mbrc.ui.navigation.library.artists.BrowseArtistView
import toothpick.Toothpick
import javax.inject.Inject

class BrowseArtistFragment : Fragment(), BrowseArtistView, ArtistEntryAdapter.MenuItemSelectedListener {

  @BindView(R.id.library_recycler) internal lateinit  var recyclerView: RecyclerView
  @Inject lateinit var adapter: ArtistEntryAdapter
  @Inject lateinit var presenter: BrowseArtistPresenter
  private lateinit var layoutManager: LinearLayoutManager
  private var scrollListener: EndlessRecyclerViewScrollListener? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_library, container, false)
    ButterKnife.bind(this, view)
    val scope = Toothpick.openScopes(context.applicationContext, this)
    Toothpick.inject(this, scope)

    presenter.bind(this)
    layoutManager = LinearLayoutManager(context)
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

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }

  override fun onResume() {
    super.onResume()
    recyclerView.addOnScrollListener(scrollListener)
  }

  override fun onPause() {
    super.onPause()
    recyclerView.removeOnScrollListener(scrollListener)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onMenuItemSelected(menuItem: MenuItem, entry: Artist) {
    when (menuItem.itemId) {
      R.id.popup_artist_queue_next -> presenter.queue(entry, Queue.NEXT)
      R.id.popup_artist_queue_last -> presenter.queue(entry, Queue.LAST)
      R.id.popup_artist_play -> presenter.queue(entry, Queue.NOW)
      R.id.popup_artist_album -> openProfile(entry)
      R.id.popup_artist_playlist -> {
      }
      else -> {
      }
    }
  }

  private fun openProfile(artist: Artist) {
    val intent = Intent(activity, ArtistAlbumsActivity::class.java)
    intent.putExtra(ArtistAlbumsActivity.ARTIST_ID, artist.id)
    intent.putExtra(ArtistAlbumsActivity.ARTIST_NAME, artist.name)
    startActivity(intent)
  }

  override fun onItemClicked(artist: Artist) {
    openProfile(artist)
  }

  override fun showEnqueueSuccess() {

  }

  override fun showEnqueueFailure() {

  }

  override fun load(artists: List<Artist>) {
    adapter.updateData(artists)
  }

  override fun clear() {
    adapter.clear()
  }

  companion object {

    fun newInstance(): BrowseArtistFragment {
      return BrowseArtistFragment()
    }
  }
}
