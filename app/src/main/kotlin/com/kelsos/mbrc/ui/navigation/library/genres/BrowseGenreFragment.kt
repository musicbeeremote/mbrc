package com.kelsos.mbrc.ui.navigation.library.genres

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
import androidx.core.view.isVisible
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.extensions.fail
import com.kelsos.mbrc.extensions.linear
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller
import kotterknife.bindView
import toothpick.Toothpick
import javax.inject.Inject

class BrowseGenreFragment : Fragment(),
  BrowseGenreView,
  MenuItemSelectedListener<GenreEntity>,
  OnRefreshListener {

  private val recycler: RecyclerView by bindView(R.id.library_browser__content)
  private val swipeLayout: SwipeRefreshLayout by bindView(R.id.library_browser__refresh_layout)
  private val fastScroller: RecyclerViewFastScroller by bindView(R.id.fastscroller)

  private val emptyView: Group by bindView(R.id.library_browser__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.library_browser__text_title)
  private val emptyViewProgress: ProgressBar by bindView(R.id.library_browser__loading_bar)

  @Inject
  lateinit var adapter: GenreEntryAdapter
  @Inject
  lateinit var actionHandler: PopupActionHandler
  @Inject
  lateinit var presenter: BrowseGenrePresenter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_browse, container, false)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val activity = activity ?: fail("null activity")
    val scope = Toothpick.openScopes(activity.application, this)
    scope.installModules(BrowseGenreModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    presenter.detach()
  }

  override fun update(pagedList: PagedList<GenreEntity>) {
    emptyView.isVisible = pagedList.isEmpty()
    adapter.submitList(pagedList)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    emptyViewTitle.setText(R.string.genres_list_empty)
    swipeLayout.setOnRefreshListener(this)
    recycler.linear(adapter, fastScroller)
    recycler.setHasFixedSize(true)
    adapter.setMenuItemSelectedListener(this)
    presenter.attach(this)
    presenter.load()
  }

  override fun onMenuItemSelected(action: String, item: GenreEntity) {
    actionHandler.genreSelected(action, item, requireContext())
  }

  override fun onItemClicked(item: GenreEntity) {
    actionHandler.genreSelected(item, requireContext())
  }

  override fun onRefresh() {
    if (!swipeLayout.isRefreshing) {
      swipeLayout.isRefreshing = true
    }

    presenter.reload()
  }

  override fun failure(throwable: Throwable) {
    swipeLayout.isRefreshing = false
    Snackbar.make(recycler, R.string.refresh_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun hideLoading() {
    emptyViewProgress.isVisible = false
    swipeLayout.isRefreshing = false
  }

  override fun updateIndexes(indexes: List<String>) {
    adapter.setIndexes(indexes)
  }
}