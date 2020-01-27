package com.kelsos.mbrc.features.library.presentation.screens

import androidx.lifecycle.LifecycleOwner
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.utilities.nonNullObserver
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.features.library.PopupActionHandler
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.presentation.LibraryViewHolder
import com.kelsos.mbrc.features.library.presentation.adapters.ArtistAdapter
import com.kelsos.mbrc.features.library.presentation.viewmodels.ArtistViewModel
import com.kelsos.mbrc.features.queue.Queue.PROFILE
import org.koin.core.KoinComponent
import org.koin.core.inject

class ArtistScreen : LibraryScreen,
  KoinComponent,
  MenuItemSelectedListener<Artist> {

  private val adapter: ArtistAdapter by inject()
  private val actionHandler: PopupActionHandler by inject()
  private val viewModel: ArtistViewModel by inject()

  private lateinit var viewHolder: LibraryViewHolder

  override fun bind(viewHolder: LibraryViewHolder) {
    this.viewHolder = viewHolder
    viewHolder.setup(R.string.artists_list_empty, adapter)
    adapter.setMenuItemSelectedListener(this)
  }

  override fun observe(viewLifecycleOwner: LifecycleOwner) {
    viewModel.artists.nonNullObserver(viewLifecycleOwner) {
      adapter.submitList(it)
      viewHolder.refreshingComplete(it.isEmpty())
    }
    viewModel.indexes.nonNullObserver(viewLifecycleOwner) {
      adapter.setIndexes(it)
    }
  }

  override fun onMenuItemSelected(action: String, item: Artist) {
    if (action == PROFILE) {
      onItemClicked(item)
      return
    }
    actionHandler.queue(action, item)
  }

  override fun onItemClicked(item: Artist) {
  }
}