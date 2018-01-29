package com.kelsos.mbrc.ui.navigation.library.tracks

import android.arch.paging.PagedList
import android.os.Bundle
import android.support.constraint.Group
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.extensions.fail
import com.kelsos.mbrc.extensions.gone
import com.kelsos.mbrc.extensions.hide
import com.kelsos.mbrc.extensions.linear
import com.kelsos.mbrc.extensions.show
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.tracks.TrackEntryAdapter.MenuItemSelectedListener
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller
import kotterknife.bindView
import toothpick.Toothpick
import javax.inject.Inject

class BrowseTrackFragment : Fragment(),
    BrowseTrackView,
    MenuItemSelectedListener,
    OnRefreshListener {

  private val recycler: RecyclerView by bindView(R.id.library_browser__content)
  private val swipeLayout: SwipeRefreshLayout by bindView(R.id.library_browser__refresh_layout)
  private val fastScroller: RecyclerViewFastScroller by bindView(R.id.fastscroller)

  private val emptyView: Group by bindView(R.id.library_browser__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.library_browser__text_title)
  private val emptyViewProgress: ProgressBar by bindView(R.id.library_browser__loading_bar)

  @Inject lateinit var adapter: TrackEntryAdapter
  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var presenter: BrowseTrackPresenter

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_browse, container, false)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val activity = activity ?: fail("null activity")
    val scope = Toothpick.openScopes(activity.application, this)
    scope.installModules(BrowseTrackModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    presenter.detach()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    emptyViewTitle.setText(R.string.tracks_list_empty)
    swipeLayout.setOnRefreshListener(this)
    recycler.linear(adapter, fastScroller, adapter)
    recycler.setHasFixedSize(true)
    adapter.setMenuItemSelectedListener(this)
    presenter.attach(this)
    presenter.load()
  }

  override fun update(pagedList: PagedList<TrackEntity>) {
    if (pagedList.isEmpty()) {
      emptyView.show()
    } else {
      emptyView.hide()
    }
    adapter.setList(pagedList)

  }

  override fun onMenuItemSelected(action: String, entry: TrackEntity) {
    actionHandler.trackSelected(action, entry)
  }

  override fun onItemClicked(track: TrackEntity) {
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
    Snackbar.make(recycler, R.string.refresh_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun hideLoading() {
    emptyViewProgress.gone()
    swipeLayout.isRefreshing = false
  }

}
