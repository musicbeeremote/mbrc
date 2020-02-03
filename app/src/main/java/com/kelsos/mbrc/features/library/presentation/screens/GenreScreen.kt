package com.kelsos.mbrc.features.library.presentation.screens

import androidx.lifecycle.LifecycleOwner
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.Meta.GENRE
import com.kelsos.mbrc.common.utilities.nonNullObserver
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.presentation.LibraryViewHolder
import com.kelsos.mbrc.features.library.presentation.adapters.GenreAdapter
import com.kelsos.mbrc.features.library.presentation.viewmodels.GenreViewModel
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.queue.Queue.DEFAULT
import com.kelsos.mbrc.features.work.WorkHandler
import org.koin.core.KoinComponent
import org.koin.core.inject

class GenreScreen : LibraryScreen,
  KoinComponent,
  MenuItemSelectedListener<Genre> {

  private val adapter: GenreAdapter by inject()
  private val workHandler: WorkHandler by inject()
  private val viewModel: GenreViewModel by inject()

  private lateinit var viewHolder: LibraryViewHolder

  override fun bind(viewHolder: LibraryViewHolder) {
    this.viewHolder = viewHolder
    viewHolder.setup(R.string.albums_list_empty, adapter)
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

  override fun onMenuItemSelected(@Queue.Action action: String, item: Genre) {
    if (action == DEFAULT) {
      onItemClicked(item)
      return
    }
    workHandler.queue(item.id, GENRE, action)
  }

  override fun onItemClicked(item: Genre) {
  }
}