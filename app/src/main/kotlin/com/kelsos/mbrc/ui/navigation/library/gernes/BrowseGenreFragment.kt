package com.kelsos.mbrc.ui.navigation.library.gernes

import android.os.Bundle
import android.support.design.widget.Snackbar
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
import com.kelsos.mbrc.adapters.GenreEntryAdapter
import com.kelsos.mbrc.adapters.GenreEntryAdapter.MenuItemSelectedListener
import com.kelsos.mbrc.data.library.Genre
import com.kelsos.mbrc.helper.PopupActionHandler
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.Toothpick
import javax.inject.Inject

class BrowseGenreFragment : Fragment(),
    BrowseGenreView,
    MenuItemSelectedListener,
    OnRefreshListener {

  @BindView(R.id.library_data_list) lateinit var recycler: EmptyRecyclerView
  @BindView(R.id.empty_view) lateinit var emptyView: View
  @BindView(R.id.swipe_layout) lateinit var swipeLayout: MultiSwipeRefreshLayout
  @BindView(R.id.list_empty_title) lateinit var emptyTitle: TextView

  @Inject lateinit var adapter: GenreEntryAdapter
  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var presenter: BrowseGenrePresenter

  override fun onCreateView(inflater: LayoutInflater?,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater!!.inflate(R.layout.fragment_library_search, container, false)
    ButterKnife.bind(this, view)
    swipeLayout.setSwipeableChildren(R.id.library_data_list, R.id.empty_view)
    emptyTitle.setText(R.string.genres_list_empty)
    return view
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val scope = Toothpick.openScopes(activity.application, activity, this)
    scope.installModules(BrowseGenreModule())
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

  override fun update(cursor: FlowCursorList<Genre>) {
    swipeLayout.isRefreshing = false
    adapter.update(cursor)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    swipeLayout.setOnRefreshListener(this)
    val layoutManager = LinearLayoutManager(activity)
    recycler.layoutManager = layoutManager
    recycler.setHasFixedSize(true)
    adapter.setMenuItemSelectedListener(this)
    recycler.adapter = adapter
    recycler.emptyView = emptyView
  }

  override fun onMenuItemSelected(menuItem: MenuItem, entry: Genre): Boolean {
    actionHandler.genreSelected(menuItem, entry, activity)
    return true
  }

  override fun onItemClicked(genre: Genre) {
    actionHandler.genreSelected(genre, activity)
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

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }
}
