package com.kelsos.mbrc.ui.navigation.library.albums

import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.PROFILE
import com.kelsos.mbrc.ui.navigation.library.LibraryScreen
import com.kelsos.mbrc.ui.navigation.library.LibraryViewHolder
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.utilities.nonNullObserver
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class AlbumScreen : LibraryScreen,
  KoinComponent,
  MenuItemSelectedListener<Album>,
  SwipeRefreshLayout.OnRefreshListener {

  private val adapter: AlbumAdapter by inject()
  private val actionHandler: PopupActionHandler by inject()
  private val viewModel: AlbumViewModel by inject()

  private lateinit var viewHolder: LibraryViewHolder

  override fun observe(viewLifecycleOwner: LifecycleOwner) {
    viewModel.albums.nonNullObserver(viewLifecycleOwner) {
      adapter.submitList(it)
      viewHolder.refreshingComplete(it.isEmpty())
    }
    viewModel.indexes.nonNullObserver(viewLifecycleOwner) {
      adapter.setIndexes(it)
    }
  }

  override fun bind(viewHolder: LibraryViewHolder) {
    this.viewHolder = viewHolder
    viewHolder.setup(R.string.albums_list_empty, this, adapter)
    adapter.setMenuItemSelectedListener(this)
  }

  override fun onMenuItemSelected(action: String, item: Album) {
    if (action == PROFILE) {
      onItemClicked(item)
      return
    }
    actionHandler.albumSelected(action, item)
  }

  override fun onItemClicked(item: Album) {
  }

  override fun onRefresh() {
    viewHolder.refreshing()
    viewModel.reload()
  }
}