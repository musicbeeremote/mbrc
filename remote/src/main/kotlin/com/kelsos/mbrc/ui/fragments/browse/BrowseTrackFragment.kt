package com.kelsos.mbrc.ui.fragments.browse

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
import com.kelsos.mbrc.adapters.EndlessRecyclerViewScrollListener
import com.kelsos.mbrc.adapters.TrackAdapter
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.presenters.BrowseTrackPresenter
import com.kelsos.mbrc.ui.views.BrowseTrackView
import roboguice.RoboGuice

class BrowseTrackFragment : Fragment(), BrowseTrackView, TrackAdapter.MenuItemSelectedListener {

  @Bind(R.id.library_recycler) internal var recyclerView: RecyclerView
  @Inject private lateinit var adapter: TrackAdapter
  @Inject private lateinit var presenter: BrowseTrackPresenter
  private var manager: LinearLayoutManager? = null
  private var scrollListener: EndlessRecyclerViewScrollListener? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    RoboGuice.getInjector(context).injectMembers(this)
    manager = LinearLayoutManager(context)
    adapter!!.setMenuItemSelectedListener(this)
    scrollListener = object : EndlessRecyclerViewScrollListener(manager) {
      override fun onLoadMore(page: Int, totalItemsCount: Int) {
        presenter!!.load(page, totalItemsCount)
      }
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_library, container, false)
    ButterKnife.bind(this, view)
    presenter!!.bind(this)
    recyclerView.layoutManager = manager
    recyclerView.adapter = adapter
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

  override fun onMenuItemSelected(item: MenuItem, track: Track) {
    when (item.itemId) {
      R.id.popup_track_play -> presenter!!.queue(track, Queue.NOW)
      R.id.popup_track_playlist -> {
      }
      R.id.popup_track_queue_next -> presenter!!.queue(track, Queue.NEXT)
      R.id.popup_track_queue_last -> presenter!!.queue(track, Queue.LAST)
      else -> {
      }
    }
  }

  override fun onItemClicked(track: Track) {
    presenter!!.queue(track, Queue.NOW)
  }

  override fun clearData() {
    adapter!!.clearData()
  }

  override fun appendPage(tracks: List<Track>) {
    adapter!!.appendData(tracks)
  }

  companion object {

    fun newInstance(): BrowseTrackFragment {
      return BrowseTrackFragment()
    }
  }
}
