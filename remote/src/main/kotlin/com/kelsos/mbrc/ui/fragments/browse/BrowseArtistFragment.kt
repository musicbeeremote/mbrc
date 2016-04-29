package com.kelsos.mbrc.ui.fragments.browse

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import butterknife.Bind
import butterknife.ButterKnife
import com.google.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.ArtistAdapter
import com.kelsos.mbrc.adapters.EndlessRecyclerViewScrollListener
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.presenters.BrowseArtistPresenter
import com.kelsos.mbrc.ui.fragments.profile.ArtistAlbumsActivity
import com.kelsos.mbrc.ui.views.BrowseArtistView
import roboguice.RoboGuice

class BrowseArtistFragment : Fragment(), BrowseArtistView, ArtistAdapter.MenuItemSelectedListener {

  @Bind(R.id.library_recycler) internal var recyclerView: RecyclerView
  @Inject private lateinit var adapter: ArtistAdapter
  @Inject private lateinit var presenter: BrowseArtistPresenter
  private var layoutManager: LinearLayoutManager? = null
  private var scrollListener: EndlessRecyclerViewScrollListener? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_library, container, false)
    ButterKnife.bind(this, view)
    RoboGuice.getInjector(context).injectMembers(this)
    presenter!!.bind(this)
    layoutManager = LinearLayoutManager(context)
    recyclerView.layoutManager = layoutManager
    recyclerView.adapter = adapter
    scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
      override fun onLoadMore(page: Int, totalItemsCount: Int) {
        presenter.load(page)
      }
    }
    adapter!!.setMenuItemSelectedListener(this)
    presenter.load()
    return view
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

  override fun onMenuItemSelected(item: MenuItem, artist: Artist) {
    when (item.itemId) {
      R.id.popup_artist_queue_next -> presenter!!.queue(artist, Queue.NEXT)
      R.id.popup_artist_queue_last -> presenter!!.queue(artist, Queue.LAST)
      R.id.popup_artist_play -> presenter!!.queue(artist, Queue.NOW)
      R.id.popup_artist_album -> openProfile(artist)
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
    adapter!!.updateData(artists)
  }

  override fun clear() {
    adapter!!.clear()
  }

  companion object {

    fun newInstance(): BrowseArtistFragment {
      return BrowseArtistFragment()
    }
  }
}
