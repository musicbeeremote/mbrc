package com.kelsos.mbrc.ui.navigation.library.artists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.PROFILE
import com.kelsos.mbrc.extensions.linear
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.artistalbums.ArtistAlbumsFragmentArgs
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller
import kotterknife.bindView
import org.koin.android.ext.android.inject

class BrowseArtistFragment : Fragment(), MenuItemSelectedListener<Artist>, OnRefreshListener {

  private val recycler: RecyclerView by bindView(R.id.library_browser__content)
  private val swipeLayout: SwipeRefreshLayout by bindView(R.id.library_browser__refresh_layout)
  private val fastScroller: RecyclerViewFastScroller by bindView(R.id.fastscroller)

  private val emptyView: Group by bindView(R.id.library_browser__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.library_browser__text_title)

  private val adapter: ArtistAdapter by inject()
  private val actionHandler: PopupActionHandler by inject()
  private val presenter: BrowseArtistViewModel by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_browse, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    swipeLayout.setOnRefreshListener(this)
    emptyViewTitle.setText(R.string.artists_list_empty)
    recycler.setHasFixedSize(true)
    recycler.linear(adapter, fastScroller)
    adapter.setMenuItemSelectedListener(this)
  }

  fun updateIndexes(indexes: List<String>) {
    adapter.setIndexes(indexes)
  }

  override fun onMenuItemSelected(action: String, item: Artist) {
    if (action == PROFILE) {
      onItemClicked(item)
      return
    }
    actionHandler.artistSelected(action, item)
  }

  override fun onItemClicked(item: Artist) {
    val args = ArtistAlbumsFragmentArgs.Builder(item.artist).build()
    view?.run {
      findNavController().navigate(R.id.artist_albums_fragment, args.toBundle())
    }
  }

  override fun onRefresh() {
    if (!swipeLayout.isRefreshing) {
      swipeLayout.isRefreshing = true
    }

    presenter.reload()
  }

  fun update(pagedList: PagedList<Artist>) {
    emptyView.isVisible = pagedList.isEmpty()
    adapter.submitList(pagedList)
  }

  fun failure(throwable: Throwable) {
    Snackbar.make(recycler, R.string.refresh_failed, Snackbar.LENGTH_SHORT).show()
  }

  fun hideLoading() {
    swipeLayout.isRefreshing = false
  }
}