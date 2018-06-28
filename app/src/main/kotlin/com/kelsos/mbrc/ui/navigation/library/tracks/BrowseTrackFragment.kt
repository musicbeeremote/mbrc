package com.kelsos.mbrc.ui.navigation.library.tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.paging.PagedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.extensions.linear
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller
import kotterknife.bindView
import org.koin.android.ext.android.inject

class BrowseTrackFragment : androidx.fragment.app.Fragment(),
  BrowseTrackView,
  MenuItemSelectedListener<TrackEntity>,
  OnRefreshListener {

  private val recycler: androidx.recyclerview.widget.RecyclerView by bindView(R.id.library_browser__content)
  private val swipeLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout by bindView(R.id.library_browser__refresh_layout)
  private val fastScroller: RecyclerViewFastScroller by bindView(R.id.fastscroller)

  private val emptyView: Group by bindView(R.id.library_browser__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.library_browser__text_title)
  private val emptyViewProgress: ProgressBar by bindView(R.id.library_browser__loading_bar)

  private val adapter: TrackEntryAdapter by inject()
  private val actionHandler: PopupActionHandler by inject()
  private val presenter: BrowseTrackPresenter by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_browse, container, false)
  }


  override fun onDestroyView() {
    super.onDestroyView()
    presenter.detach()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    emptyViewTitle.setText(R.string.tracks_list_empty)
    swipeLayout.setOnRefreshListener(this)
    recycler.linear(adapter, fastScroller)
    recycler.setHasFixedSize(true)
    adapter.setMenuItemSelectedListener(this)
    presenter.attach(this)
    presenter.load()
  }

  override fun updateIndexes(indexes: List<String>) {
    adapter.setIndexes(indexes)
  }

  override fun update(pagedList: PagedList<TrackEntity>) {
    emptyView.isVisible = pagedList.isEmpty()
    adapter.submitList(pagedList)
  }

  override fun onMenuItemSelected(action: String, item: TrackEntity) {
    actionHandler.trackSelected(action, item)
  }

  override fun onItemClicked(item: TrackEntity) {
    actionHandler.trackSelected(item)
  }

  override fun onRefresh() {
    if (!swipeLayout.isRefreshing) {
      swipeLayout.isRefreshing = true
    }

    presenter.reload()
  }

  override fun failure(it: Throwable) {
    swipeLayout.isRefreshing = false
    com.google.android.material.snackbar.Snackbar.make(recycler, R.string.refresh_failed, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
  }

  override fun hideLoading() {
    emptyViewProgress.isVisible = false
    swipeLayout.isRefreshing = false
  }
}