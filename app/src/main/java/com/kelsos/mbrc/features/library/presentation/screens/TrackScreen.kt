package com.kelsos.mbrc.features.library.presentation.screens

import androidx.lifecycle.LifecycleOwner
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.common.utilities.nonNullObserver
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.presentation.LibraryViewHolder
import com.kelsos.mbrc.features.library.presentation.adapters.TrackAdapter
import com.kelsos.mbrc.features.library.presentation.viewmodels.TrackViewModel
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.work.WorkHandler

class TrackScreen(
  private val adapter: TrackAdapter,
  private val workHandler: WorkHandler,
  private val viewModel: TrackViewModel,
) : LibraryScreen,
  MenuItemSelectedListener<Track> {
  private var viewHolder: LibraryViewHolder? = null

  override fun bind(viewHolder: LibraryViewHolder) {
    this.viewHolder = viewHolder
    viewHolder.setup(R.string.albums_list_empty, adapter)
    adapter.setMenuItemSelectedListener(this)
  }

  override fun observe(viewLifecycleOwner: LifecycleOwner) {
    viewModel.tracks.nonNullObserver(viewLifecycleOwner) {
      adapter.submitList(it)
      viewHolder?.refreshingComplete(it.isEmpty())
    }
  }

  override fun onMenuItemSelected(action: Queue, item: Track) {
    workHandler.queue(item.id, Meta.Track, action = action)
  }

  override fun onItemClicked(item: Track) {
    workHandler.queue(item.id, Meta.Track)
  }
}
