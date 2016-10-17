package com.kelsos.mbrc.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.ArtistEntryAdapter
import com.kelsos.mbrc.adapters.ArtistEntryAdapter.MenuItemSelectedListener
import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.extensions.initLinear
import com.kelsos.mbrc.helper.PopupActionHandler
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class BrowseArtistFragment : Fragment(),
    BrowseArtistView,
    MenuItemSelectedListener,
    OnRefreshListener {

  @BindView(R.id.library_data_list) lateinit var recycler: EmptyRecyclerView
  @BindView(R.id.empty_view) lateinit var emptyView: View
  @BindView(R.id.swipe_layout) lateinit var swipeLayout: MultiSwipeRefreshLayout
  @BindView(R.id.list_empty_title) lateinit var emptyTitle: TextView

  @Inject lateinit var bus: RxBus
  @Inject lateinit var adapter: ArtistEntryAdapter
  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var presenter: BrowseArtistPresenter

  private var scope: Scope? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(activity.application, activity, this)
    scope?.installModules(BrowseArtistModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater!!.inflate(R.layout.fragment_library_search, container, false)
    ButterKnife.bind(this, view)
    swipeLayout.setOnRefreshListener(this)
    swipeLayout.setSwipeableChildren(R.id.library_data_list, R.id.empty_view)
    emptyTitle.setText(R.string.artists_list_empty)
    return view
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    recycler.setHasFixedSize(true)
    recycler.initLinear(adapter, emptyView)
    adapter.setMenuItemSelectedListener(this)
    presenter.attach(this)
    presenter.load()
  }

  override fun onMenuItemSelected(menuItem: MenuItem, entry: Artist) {
    actionHandler.artistSelected(menuItem, entry, activity)
  }

  override fun onItemClicked(artist: Artist) {
    actionHandler.artistSelected(artist, activity)
  }

  override fun onRefresh() {
    if (!swipeLayout.isRefreshing) {
      swipeLayout.isRefreshing = true
    }

    presenter.reload()
  }

  override fun update(data: FlowCursorList<Artist>) {
    adapter.update(data)
  }
}
