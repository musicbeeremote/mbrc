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
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.AlbumEntryAdapter
import com.kelsos.mbrc.adapters.EndlessGridRecyclerViewScrollListener
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.presenters.BrowseAlbumPresenter
import com.kelsos.mbrc.ui.fragments.profile.AlbumTracksActivity
import com.kelsos.mbrc.ui.views.BrowseAlbumView
import toothpick.Toothpick
import javax.inject.Inject

class BrowseAlbumFragment : Fragment(), AlbumEntryAdapter.MenuItemSelectedListener, BrowseAlbumView {

  @BindView(R.id.album_recycler) internal lateinit var recyclerView: RecyclerView
  @Inject lateinit var adapter: AlbumEntryAdapter
  @Inject lateinit var presenter: BrowseAlbumPresenter
  private var scrollListener: EndlessGridRecyclerViewScrollListener? = null

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater!!.inflate(R.layout.ui_library_grid, container, false)
    ButterKnife.bind(this, view)
    val scope = Toothpick.openScopes(context.applicationContext, this)
    Toothpick.inject(this, scope)

    presenter.attach(this)
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
    presenter.detach()
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
