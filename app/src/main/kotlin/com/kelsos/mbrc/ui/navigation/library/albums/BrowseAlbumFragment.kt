package com.kelsos.mbrc.ui.navigation.library.albums

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.extensions.fail
import com.kelsos.mbrc.extensions.initLinear
import com.kelsos.mbrc.ui.dialogs.SortingDialog
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller
import kotterknife.bindView
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class BrowseAlbumFragment : Fragment(),
    BrowseAlbumView,
    AlbumEntryAdapter.MenuItemSelectedListener,
    SwipeRefreshLayout.OnRefreshListener {

  private val recycler: EmptyRecyclerView by bindView(R.id.library_data_list)
  private val swipeLayout: MultiSwipeRefreshLayout by bindView(R.id.swipe_layout)
  private val fastScroller: RecyclerViewFastScroller by bindView(R.id.fastscroller)

  private val emptyView: View by bindView(R.id.empty_view)
  private val emptyViewTitle: TextView by bindView(R.id.list_empty_title)
  private val emptyViewIcon: ImageView by bindView(R.id.list_empty_icon)
  private val emptyViewSubTitle: TextView by bindView(R.id.list_empty_subtitle)
  private val emptyViewProgress: ProgressBar by bindView(R.id.empty_view_progress_bar)

  @Inject lateinit var adapter: AlbumEntryAdapter
  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var presenter: BrowseAlbumPresenter

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_browse, container, false)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val activity = activity ?: fail("null activity")
    val scope = Toothpick.openScopes(activity.application, activity, this)
    scope.installModules(SmoothieActivityModule(activity), BrowseAlbumModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    presenter.attach(this)
    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
    inflater?.inflate(R.menu.browse_album__menu, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    if (item?.itemId == R.id.browse_album__sort_albums) {
      presenter.showSorting()
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  override fun showSorting(order: Long, selection: Long) {
    val fm = fragmentManager ?: fail("null fragmentManager")
    SortingDialog.create(fm, order, selection, {
      presenter.order(it)
    }, {
      presenter.sortBy(it)
    }
    ).show()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    swipeLayout.setOnRefreshListener(this)
    swipeLayout.setSwipeableChildren(R.id.library_data_list, R.id.empty_view)
    emptyViewTitle.setText(R.string.albums_list_empty)
    recycler.initLinear(adapter, emptyView, fastScroller)
    recycler.setHasFixedSize(true)
    adapter.setMenuItemSelectedListener(this)
    presenter.attach(this)
    presenter.load()
  }

  override fun onMenuItemSelected(menuItem: MenuItem, entry: AlbumEntity) {
    val activity = activity ?: fail("null activity")
    actionHandler.albumSelected(menuItem, entry, activity)
  }

  override fun onItemClicked(album: AlbumEntity) {
    val activity = activity ?: fail("null activity")
    actionHandler.albumSelected(album, activity)
  }

  override fun onRefresh() {
    if (!swipeLayout.isRefreshing) {
      swipeLayout.isRefreshing = true
    }

    presenter.reload()
  }

  override fun update(cursor: List<AlbumEntity>) {
    adapter.update(cursor)
    swipeLayout.isRefreshing = false
  }

  override fun failure(throwable: Throwable) {
    swipeLayout.isRefreshing = false
    Snackbar.make(recycler, R.string.refresh_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun showLoading() {
    emptyViewProgress.visibility = View.VISIBLE
    emptyViewIcon.visibility = View.GONE
    emptyViewTitle.visibility = View.GONE
    emptyViewSubTitle.visibility = View.GONE
  }

  override fun hideLoading() {
    emptyViewProgress.visibility = View.GONE
    emptyViewIcon.visibility = View.VISIBLE
    emptyViewTitle.visibility = View.VISIBLE
    emptyViewSubTitle.visibility = View.VISIBLE
    swipeLayout.isRefreshing = false
  }

  override fun onDestroyView() {
    super.onDestroyView()
    presenter.detach()
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }

}
