package com.kelsos.mbrc.ui.fragments.browse

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.google.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.AlbumAdapter
import com.kelsos.mbrc.adapters.EndlessGridRecyclerViewScrollListener
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.presenters.BrowseAlbumPresenter
import com.kelsos.mbrc.ui.fragments.profile.AlbumTracksActivity
import com.kelsos.mbrc.ui.views.BrowseAlbumView
import roboguice.RoboGuice

class BrowseAlbumFragment : Fragment(), AlbumAdapter.MenuItemSelectedListener, BrowseAlbumView {

  @BindView(R.id.album_recycler) internal lateinit var recyclerView: RecyclerView
  @Inject private lateinit var adapter: AlbumAdapter
  @Inject private lateinit var presenter: BrowseAlbumPresenter
  private var scrollListener: EndlessGridRecyclerViewScrollListener? = null

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater!!.inflate(R.layout.ui_library_grid, container, false)
    ButterKnife.bind(this, view)
    RoboGuice.getInjector(context).injectMembers(this)
    presenter.bind(this)
    val layoutManager = GridLayoutManager(context, 2)
    scrollListener = object : EndlessGridRecyclerViewScrollListener(layoutManager) {
      override fun onLoadMore(page: Int, totalItemsCount: Int) {
        presenter.load(page)
      }
    }
    recyclerView.layoutManager = layoutManager
    recyclerView.adapter = adapter
    adapter.setMenuItemSelectedListener(this)
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

  override fun onMenuItemSelected(menuItem: MenuItem, album: Album): Boolean {
    when (menuItem.itemId) {
      R.id.popup_album_tracks -> openProfile(album)
      R.id.popup_album_play -> presenter.queue(album, Queue.NOW)
      R.id.popup_album_queue_last -> presenter.queue(album, Queue.LAST)
      R.id.popup_album_queue_next -> presenter.queue(album, Queue.NEXT)
      R.id.popup_album_playlist -> {
      }
      else -> {
        return false
      }
    }

    return true
  }

  override fun onItemClicked(album: Album) {
    openProfile(album)
  }

  private fun openProfile(album: Album) {
    val intent = Intent(context, AlbumTracksActivity::class.java)
    val bundle = Bundle()
    bundle.putLong(AlbumTracksActivity.ALBUM_ID, album.id)
    intent.putExtras(bundle)
    startActivity(intent)
  }

  override fun updateData(data: List<Album>) {
    adapter.updateData(data)
  }

  override fun clearData() {
    adapter.clearData()
  }

  companion object {

    fun newInstance(): BrowseAlbumFragment {
      return BrowseAlbumFragment()
    }
  }
}
