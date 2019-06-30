package com.kelsos.mbrc.ui.navigation.library.tracks

import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.ui.navigation.library.LibraryScreen
import com.kelsos.mbrc.ui.navigation.library.LibraryViewHolder
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.utilities.nonNullObserver
import org.koin.core.KoinComponent
import org.koin.core.inject

class TrackScreen : LibraryScreen,
  KoinComponent,
  MenuItemSelectedListener<Track>,
  OnRefreshListener {

  private val adapter: TrackAdapter by inject()
  private val actionHandler: PopupActionHandler by inject()
  private val viewModel: TrackViewModel by inject()

  private lateinit var viewHolder: LibraryViewHolder

  override fun bind(viewHolder: LibraryViewHolder) {
    this.viewHolder = viewHolder
    viewHolder.setup(R.string.albums_list_empty, this, adapter)
    adapter.setMenuItemSelectedListener(this)
  }

  override fun observe(viewLifecycleOwner: LifecycleOwner) {
    viewModel.tracks.nonNullObserver(viewLifecycleOwner) {
      adapter.submitList(it)
      viewHolder.refreshingComplete(it.isEmpty())
    }
    viewModel.indexes.nonNullObserver(viewLifecycleOwner) {
      adapter.setIndexes(it)
    }
  }

  override fun onMenuItemSelected(action: String, item: Track) {
    actionHandler.trackSelected(action, item)
  }

  override fun onItemClicked(item: Track) {
    actionHandler.trackSelected(item)
  }

  override fun onRefresh() {
    viewHolder.refreshing()
    viewModel.reload()
  }
}