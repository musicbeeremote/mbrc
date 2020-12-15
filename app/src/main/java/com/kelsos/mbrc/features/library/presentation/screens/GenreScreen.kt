package com.kelsos.mbrc.features.library.presentation.screens

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.Meta.GENRE
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.features.library.PopupActionHandler
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.presentation.LibraryViewHolder
import com.kelsos.mbrc.features.library.presentation.adapters.GenreAdapter
import com.kelsos.mbrc.features.library.presentation.viewmodels.GenreViewModel
import com.kelsos.mbrc.features.queue.Queue.DEFAULT
import com.kelsos.mbrc.features.work.WorkHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class GenreScreen(
  private val adapter: GenreAdapter,
  private val workHandler: WorkHandler,
  private val viewModel: GenreViewModel,
  private val actionHandler: PopupActionHandler
) : LibraryScreen, MenuItemSelectedListener<Genre> {

  private lateinit var viewHolder: LibraryViewHolder

  override fun bind(viewHolder: LibraryViewHolder) {
    this.viewHolder = viewHolder
    viewHolder.setup(R.string.albums_list_empty, adapter)
    adapter.setMenuItemSelectedListener(this)
  }

  override fun observe(viewLifecycleOwner: LifecycleOwner) {
    val lifecycleScope = viewLifecycleOwner.lifecycleScope
    lifecycleScope.launch {
      adapter.loadStateFlow.drop(1).distinctUntilChangedBy { it.refresh }.collect {
        viewHolder.refreshingComplete(adapter.itemCount == 0)
      }
    }
    lifecycleScope.launch {
      viewModel.genres.collect {
        adapter.submitData(it)
      }
    }
  }

  override fun onMenuItemSelected(itemId: Int, item: Genre) {
    val action = actionHandler.genreSelected(itemId)
    if (action == DEFAULT) {
      onItemClicked(item)
    } else {
      workHandler.queue(item.id, GENRE, action)
    }
  }

  override fun onItemClicked(item: Genre) {
    workHandler.queue(item.id, GENRE)
  }
}
