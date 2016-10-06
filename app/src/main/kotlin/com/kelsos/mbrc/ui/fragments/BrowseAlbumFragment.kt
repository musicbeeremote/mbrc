package com.kelsos.mbrc.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.AlbumEntryAdapter
import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.NotifyUser
import com.kelsos.mbrc.helper.PopupActionHandler
import com.kelsos.mbrc.services.BrowseSync
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class BrowseAlbumFragment : Fragment(), AlbumEntryAdapter.MenuItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
  @BindView(R.id.library_data_list) lateinit var recycler: EmptyRecyclerView
  @BindView(R.id.empty_view) lateinit var emptyView: View
  @BindView(R.id.swipe_layout) lateinit var swipeLayout: MultiSwipeRefreshLayout
  @BindView(R.id.list_empty_title) lateinit var emptyTitle: TextView

  @Inject lateinit var adapter: AlbumEntryAdapter
  @Inject lateinit var bus: RxBus
  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var sync: BrowseSync

  private var subscription: Subscription? = null

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
    adapter.init(null)
  }

  override fun onResume() {
    super.onResume()
    adapter.refresh()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val activity = activity
    val scope = Toothpick.openScopes(activity.application, activity, this)
    scope.installModules(SmoothieActivityModule(activity))
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
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

    if (subscription != null && !subscription!!.isUnsubscribed) {
      return
    }

    subscription = sync.syncAlbums(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnTerminate { swipeLayout.isRefreshing = false }
        .subscribe({ adapter.refresh() }) {
          bus.post(NotifyUser(R.string.refresh_failed))
          Timber.v(it, "failed")
        }
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }
}
