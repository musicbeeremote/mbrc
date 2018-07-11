package com.kelsos.mbrc.ui.navigation.library.albums

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.PROFILE
import com.kelsos.mbrc.extensions.linear
import com.kelsos.mbrc.ui.dialogs.SortingDialog
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.albumtracks.AlbumTracksFragmentArgs
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller
import kotterknife.bindView
import org.koin.android.ext.android.inject


class BrowseAlbumFragment : Fragment(),
  MenuItemSelectedListener<AlbumEntity>,
  SwipeRefreshLayout.OnRefreshListener {

  private val recycler: RecyclerView by bindView(R.id.library_browser__content)
  private val swipeLayout: SwipeRefreshLayout by bindView(R.id.library_browser__refresh_layout)
  private val fastScroller: RecyclerViewFastScroller by bindView(R.id.fastscroller)

  private val emptyView: Group by bindView(R.id.library_browser__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.library_browser__text_title)
  private val emptyViewProgress: ProgressBar by bindView(R.id.library_browser__loading_bar)

  private val adapter: AlbumEntryAdapter by inject()
  private val actionHandler: PopupActionHandler by inject()
  private val presenter: BrowseAlbumViewModel by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_browse, container, false)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
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

  fun showSorting(order: Int, selection: Int) {
    with(requireFragmentManager()) {
      SortingDialog.create(this, selection, order, presenter::order, presenter::sortBy).show()
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    swipeLayout.setOnRefreshListener(this)

    emptyViewTitle.setText(R.string.albums_list_empty)
    recycler.linear(adapter, fastScroller)
    recycler.setHasFixedSize(true)
    adapter.setMenuItemSelectedListener(this)
  }

  override fun onMenuItemSelected(action: String, item: AlbumEntity) {
    if (action == PROFILE) {
      onItemClicked(item)
      return
    }
    actionHandler.albumSelected(action, item)
  }

  override fun onItemClicked(item: AlbumEntity) {
    val args = AlbumTracksFragmentArgs.Builder(item.album, item.artist).build()
    findNavController(this).navigate(R.id.album_tracks_fragment, args.toBundle())
  }

  override fun onRefresh() {
    if (!swipeLayout.isRefreshing) {
      swipeLayout.isRefreshing = true
    }

    //presenter.refresh()
  }

  fun update(pagedList: PagedList<AlbumEntity>) {
    emptyView.isVisible = pagedList.isEmpty()
    adapter.submitList(pagedList)
    swipeLayout.isRefreshing = false
  }

  fun updateIndexes(indexes: List<String>) {
    adapter.setIndexes(indexes)
  }

  fun failure(throwable: Throwable) {
    swipeLayout.isRefreshing = false
    Snackbar.make(recycler, R.string.refresh_failed, Snackbar.LENGTH_SHORT).show()
  }

  fun hideLoading() {
    emptyViewProgress.isVisible = false
    swipeLayout.isRefreshing = false
  }
}