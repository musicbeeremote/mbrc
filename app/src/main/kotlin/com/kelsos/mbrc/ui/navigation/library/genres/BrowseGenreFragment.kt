package com.kelsos.mbrc.ui.navigation.library.genres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.paging.PagedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar.make
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.PROFILE
import com.kelsos.mbrc.extensions.linear
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.genreartists.GenreArtistsFragmentArgs
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller
import kotterknife.bindView
import org.koin.android.ext.android.inject


class BrowseGenreFragment : androidx.fragment.app.Fragment(),
  BrowseGenreView,
  MenuItemSelectedListener<GenreEntity>,
  OnRefreshListener {

  private val recycler: androidx.recyclerview.widget.RecyclerView by bindView(R.id.library_browser__content)
  private val swipeLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout by bindView(R.id.library_browser__refresh_layout)
  private val fastScroller: RecyclerViewFastScroller by bindView(R.id.fastscroller)

  private val emptyView: Group by bindView(R.id.library_browser__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.library_browser__text_title)
  private val emptyViewProgress: ProgressBar by bindView(R.id.library_browser__loading_bar)

  private val adapter: GenreEntryAdapter by inject()
  private val actionHandler: PopupActionHandler by inject()
  private val presenter: BrowseGenrePresenter by inject()

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
    if (action == PROFILE) {
      onItemClicked(item)
      return
    }
    actionHandler.genreSelected(action, item)
  }

  override fun onItemClicked(item: GenreEntity) {
    val args = GenreArtistsFragmentArgs.Builder(item.genre).build()
    findNavController(this).navigate(R.id.genre_artists_fragment, args.toBundle())
  }

  override fun onRefresh() {
    if (!swipeLayout.isRefreshing) {
      swipeLayout.isRefreshing = true
    }

    presenter.reload()
  }

  override fun failure(throwable: Throwable) {
    swipeLayout.isRefreshing = false
    make(recycler, R.string.refresh_failed, LENGTH_SHORT).show()
  }

  override fun hideLoading() {
    emptyViewProgress.isVisible = false
    swipeLayout.isRefreshing = false
  }

  override fun updateIndexes(indexes: List<String>) {
    adapter.setIndexes(indexes)
  }
}