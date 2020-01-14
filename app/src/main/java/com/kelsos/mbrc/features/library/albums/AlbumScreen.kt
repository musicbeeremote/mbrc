package com.kelsos.mbrc.features.library.albums

import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.queue.LibraryPopup.PROFILE
import com.kelsos.mbrc.features.library.LibraryResult
import com.kelsos.mbrc.features.library.LibraryScreen
import com.kelsos.mbrc.features.library.LibraryViewHolder
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.features.library.PopupActionHandler
import com.kelsos.mbrc.utilities.nonNullObserver
import org.koin.core.KoinComponent
import org.koin.core.inject

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
    viewModel.emitter.nonNullObserver(viewLifecycleOwner) {
      it.contentIfNotHandled?.let { result ->
        when (result) {
          LibraryResult.RefreshSuccess -> {
            viewHolder.refreshing(false)
          }
          LibraryResult.RefreshFailure -> {
            viewHolder.refreshing(false)
          }
        }
      }
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