package com.kelsos.mbrc.features.library.presentation.screens

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.features.library.PopupActionHandler
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.presentation.LibraryViewHolder
import com.kelsos.mbrc.features.library.presentation.TrackAdapter
import com.kelsos.mbrc.features.library.presentation.TrackViewModel
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.work.WorkHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class TrackScreen(
  private val adapter: TrackAdapter,
  private val workHandler: WorkHandler,
  private val viewModel: TrackViewModel,
  private val actionHandler: PopupActionHandler
) : LibraryScreen, MenuItemSelectedListener<Track> {

  private var viewHolder: LibraryViewHolder? = null

  override fun bind(viewHolder: LibraryViewHolder) {
    this.viewHolder = viewHolder
    viewHolder.setup(R.string.albums_list_empty, adapter)
    adapter.setMenuItemSelectedListener(this)
    adapter.setCoverMode(true)
  }

  override fun observe(viewLifecycleOwner: LifecycleOwner) {
    val lifecycleScope = viewLifecycleOwner.lifecycleScope
    lifecycleScope.launch {
      adapter.loadStateFlow.drop(1).distinctUntilChangedBy { it.refresh }.collect {
        viewHolder?.refreshingComplete(adapter.itemCount == 0)
      }
    }
    lifecycleScope.launch {
      viewModel.tracks.collect {
        adapter.submitData(it)
      }
    }
  }

  override fun onMenuItemSelected(itemId: Int, item: Track) {
    val action = actionHandler.genreSelected(itemId)
    if (action === Queue.Default) {
      onItemClicked(item)
    } else {
      workHandler.queue(item.id, Meta.Track, action = action)
    }
  }

  override fun onItemClicked(item: Track) {
    workHandler.queue(item.id, Meta.Track)
  }
}
