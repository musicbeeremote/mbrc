package com.kelsos.mbrc.ui.navigation.library.genres

import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.features.queue.LibraryPopup.PROFILE
import com.kelsos.mbrc.ui.navigation.library.LibraryScreen
import com.kelsos.mbrc.ui.navigation.library.LibraryViewHolder
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.utilities.nonNullObserver
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class GenreScreen : LibraryScreen,
  KoinComponent,
  MenuItemSelectedListener<Genre>,
  OnRefreshListener {

  private val adapter: GenreAdapter by inject()
  private val actionHandler: PopupActionHandler by inject()
  private val viewModel: GenreViewModel by inject()

  private lateinit var viewHolder: LibraryViewHolder

  override fun bind(viewHolder: LibraryViewHolder) {
    this.viewHolder = viewHolder
    viewHolder.setup(R.string.albums_list_empty, this, adapter)
    adapter.setMenuItemSelectedListener(this)
  }

  override fun observe(viewLifecycleOwner: LifecycleOwner) {
    viewModel.genres.nonNullObserver(viewLifecycleOwner) {
      adapter.submitList(it)
      viewHolder.refreshingComplete(it.isEmpty())
    }
    viewModel.indexes.nonNullObserver(viewLifecycleOwner) {
      adapter.setIndexes(it)
    }
  }

  override fun onMenuItemSelected(action: String, item: Genre) {
    if (action == PROFILE) {
      onItemClicked(item)
      return
    }
    actionHandler.genreSelected(action, item)
  }

  override fun onItemClicked(item: Genre) {
  }

  override fun onRefresh() {
    viewHolder.refreshing()
    viewModel.reload()
  }
}