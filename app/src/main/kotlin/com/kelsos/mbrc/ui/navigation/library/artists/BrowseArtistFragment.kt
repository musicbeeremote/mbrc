package com.kelsos.mbrc.ui.navigation.library.artists

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.extensions.fail
import com.kelsos.mbrc.extensions.initLinear
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.artists.ArtistEntryAdapter.MenuItemSelectedListener
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class BrowseArtistFragment : Fragment(),
    BrowseArtistView,
    MenuItemSelectedListener,
    OnRefreshListener {

  private val recycler: EmptyRecyclerView by bindView(R.id.library_data_list)
  private val swipeLayout: MultiSwipeRefreshLayout by bindView(R.id.swipe_layout)
  private val fastScroller: RecyclerViewFastScroller by bindView(R.id.fastscroller)

  private val emptyView: View by bindView(R.id.empty_view)
  private val emptyViewTitle: TextView by bindView(R.id.list_empty_title)
  private val emptyViewIcon: ImageView by bindView(R.id.list_empty_icon)
  private val emptyViewSubTitle: TextView by bindView(R.id.list_empty_subtitle)
  private val emptyViewProgress: ProgressBar by bindView(R.id.empty_view_progress_bar)

  @Inject lateinit var adapter: ArtistEntryAdapter
  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var presenter: BrowseArtistPresenter

  private var scope: Scope? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    val activity = activity ?: fail("null activity")
    scope = Toothpick.openScopes(activity.application, activity, this)
    scope?.installModules(BrowseArtistModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    presenter.detach()
  }
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_browse, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    swipeLayout.setOnRefreshListener(this)
    swipeLayout.setSwipeableChildren(R.id.library_data_list, R.id.empty_view)
    emptyViewTitle.setText(R.string.artists_list_empty)
    recycler.setHasFixedSize(true)
    recycler.initLinear(adapter, emptyView, fastScroller)
    adapter.setMenuItemSelectedListener(this)
    presenter.attach(this)
    presenter.load()
  }

  override fun onMenuItemSelected(menuItem: MenuItem, entry: ArtistEntity) {
    val activity = activity ?: fail("null activity")
    actionHandler.artistSelected(menuItem, entry, activity)
  }

  override fun onItemClicked(artist: ArtistEntity) {
    val activity = activity ?: fail("null activity")
    actionHandler.artistSelected(artist, activity)
  }

  override fun onRefresh() {
    if (!swipeLayout.isRefreshing) {
      swipeLayout.isRefreshing = true
    }

    presenter.reload()
  }

  override fun update(data: List<ArtistEntity>) {
    swipeLayout.isRefreshing = false
    adapter.update(data)
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

}
