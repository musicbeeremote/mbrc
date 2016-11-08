package com.kelsos.mbrc.ui.navigation.library.albums

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.AlbumEntryAdapter
import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.helper.PopupActionHandler
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class BrowseAlbumFragment : Fragment(),
    BrowseAlbumView,
    AlbumEntryAdapter.MenuItemSelectedListener,
    SwipeRefreshLayout.OnRefreshListener {

  @BindView(R.id.library_data_list) lateinit var recycler: EmptyRecyclerView
  @BindView(R.id.empty_view) lateinit var emptyView: View
  @BindView(R.id.swipe_layout) lateinit var swipeLayout: MultiSwipeRefreshLayout
  @BindView(R.id.list_empty_title) lateinit var emptyTitle: TextView

  @Inject lateinit var adapter: AlbumEntryAdapter
  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var presenter: BrowseAlbumPresenter

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater!!.inflate(R.layout.fragment_library_search, container, false)
    ButterKnife.bind(this, view)
    swipeLayout.setOnRefreshListener(this)
    swipeLayout.setSwipeableChildren(R.id.library_data_list, R.id.empty_view)
    emptyTitle.setText(R.string.albums_list_empty)
    return view
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)

  }

  override fun onResume() {
    super.onResume()
    adapter.refresh()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val scope = Toothpick.openScopes(activity.application, activity, this)
    scope.installModules(SmoothieActivityModule(activity),
        BrowseAlbumModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    presenter.attach(this)
    presenter.load()
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    recycler.setHasFixedSize(true)
    val mLayoutManager = LinearLayoutManager(activity)
    recycler.layoutManager = mLayoutManager
    adapter.setMenuItemSelectedListener(this)
    recycler.adapter = adapter
    recycler.emptyView = emptyView
  }

  override fun onMenuItemSelected(menuItem: MenuItem, entry: Album) {
    actionHandler.albumSelected(menuItem, entry, activity)
  }

  override fun onItemClicked(album: Album) {
    actionHandler.albumSelected(album, activity)
  }

  override fun onRefresh() {
    if (!swipeLayout.isRefreshing) {
      swipeLayout.isRefreshing = true
    }

    presenter.reload()
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
  }

  override fun update(cursor: FlowCursorList<Album>) {
    adapter.update(cursor)
    swipeLayout.isRefreshing = false
  }

  override fun failure(throwable: Throwable) {
    swipeLayout.isRefreshing = false
    Snackbar.make(recycler, R.string.refresh_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }
}
