package com.kelsos.mbrc.features.library.presentation.screens

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.Meta.ALBUM
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.features.library.PopupActionHandler
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.presentation.LibraryViewHolder
import com.kelsos.mbrc.features.library.presentation.adapters.AlbumAdapter
import com.kelsos.mbrc.features.library.presentation.viewmodels.AlbumViewModel
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.work.WorkHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class AlbumScreen(
  private val adapter: AlbumAdapter,
  private val workHandler: WorkHandler,
  private val viewModel: AlbumViewModel,
  private val actionHandler: PopupActionHandler
) : LibraryScreen, MenuItemSelectedListener<Album> {

  private lateinit var viewHolder: LibraryViewHolder

  override fun observe(viewLifecycleOwner: LifecycleOwner) {
    val lifecycleScope = viewLifecycleOwner.lifecycleScope
    lifecycleScope.launch {
      adapter.loadStateFlow.drop(1).distinctUntilChangedBy { it.refresh }.collect {
        viewHolder.refreshingComplete(adapter.itemCount == 0)
      }
    }
    lifecycleScope.launch {
      viewModel.albums.collect {
        adapter.submitData(it)
      }
    }
  }

  override fun bind(viewHolder: LibraryViewHolder) {
    this.viewHolder = viewHolder
    viewHolder.setup(R.string.albums_list_empty, adapter)
    adapter.setMenuItemSelectedListener(this)
  }

  override fun onMenuItemSelected(itemId: Int, item: Album) {
    val action = actionHandler.genreSelected(itemId)
    if (action === Queue.DEFAULT) {
      onItemClicked(item)
    } else {
      workHandler.queue(item.id, ALBUM, action)
    }
  }

  override fun onItemClicked(item: Album) {
    workHandler.queue(item.id, ALBUM)
  }
}
