package com.kelsos.mbrc.features.library.presentation.screens

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.features.library.PopupActionHandler
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.presentation.LibraryViewHolder
import com.kelsos.mbrc.features.library.presentation.adapters.GenreAdapter
import com.kelsos.mbrc.features.library.presentation.viewmodels.GenreViewModel
import com.kelsos.mbrc.features.queue.Queue
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
class GenreScreen : LibraryScreen, KoinComponent, MenuItemSelectedListener<Genre> {

  private val adapter: GenreAdapter by inject()
  private val actionHandler: PopupActionHandler by inject()
  private val viewModel: GenreViewModel by inject()

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
    if (action === Queue.DEFAULT) {
      onItemClicked(item)
    }
  }

  override fun onItemClicked(item: Genre) {
  }
}
