package com.kelsos.mbrc.ui.navigation.library.tracks

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.TrackEntryAdapter
import com.kelsos.mbrc.adapters.TrackEntryAdapter.MenuItemSelectedListener
import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.helper.PopupActionHandler
import com.kelsos.mbrc.services.BrowseSync
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.Toothpick
import javax.inject.Inject

class BrowseTrackFragment : Fragment(),
    BrowseTrackView,
    MenuItemSelectedListener,
    OnRefreshListener {

  @BindView(R.id.swipe_layout) lateinit var swipeLayout: MultiSwipeRefreshLayout
  @BindView(R.id.library_data_list) lateinit var recycler: EmptyRecyclerView
  @BindView(R.id.empty_view) lateinit var emptyView: View
  @BindView(R.id.list_empty_title) lateinit var emptyTitle: TextView

  @Inject lateinit var bus: RxBus
  @Inject lateinit var adapter: TrackEntryAdapter
  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var sync: BrowseSync
  @Inject lateinit var presenter: BrowseTrackPresenter

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater!!.inflate(R.layout.fragment_library_search, container, false)
    ButterKnife.bind(this, view)
    swipeLayout.setSwipeableChildren(R.id.library_data_list, R.id.empty_view)
    emptyTitle.setText(R.string.tracks_list_empty)
    return view
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val scope = Toothpick.openScopes(activity.application, activity, this)
    scope.installModules(BrowseTrackModule())
    Toothpick.inject(this, scope)
    presenter.attach(this)
    presenter.load()
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    swipeLayout.setOnRefreshListener(this)
    val layoutManager = LinearLayoutManager(activity)
    recycler.setHasFixedSize(true)
    recycler.layoutManager = layoutManager
    adapter.setMenuItemSelectedListener(this)
    recycler.adapter = adapter
    recycler.emptyView = emptyView
  }

  override fun update(it: FlowCursorList<Track>) {
    adapter.update(it)
    swipeLayout.isRefreshing = false
  }

  override fun onMenuItemSelected(menuItem: MenuItem, entry: Track) {
    actionHandler.trackSelected(menuItem, entry)
  }

  override fun onItemClicked(track: Track) {
    actionHandler.trackSelected(track)
  }

  override fun onRefresh() {
    if (!swipeLayout.isRefreshing) {
      swipeLayout.isRefreshing = true
    }

    presenter.reload()
  }

  override fun failure(it: Throwable) {
    swipeLayout.isRefreshing = false
  }
}
