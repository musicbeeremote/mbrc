package com.kelsos.mbrc.features.library.presentation.screens

import androidx.lifecycle.LifecycleOwner
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.common.utilities.nonNullObserver
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.presentation.LibraryViewHolder
import com.kelsos.mbrc.features.library.presentation.adapters.GenreAdapter
import com.kelsos.mbrc.features.library.presentation.viewmodels.GenreViewModel
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.queue.Queue.Default
import com.kelsos.mbrc.features.work.WorkHandler

typealias OnGenrePressed = (genre: Genre) -> Unit

class GenreScreen(
  private val adapter: GenreAdapter,
  private val workHandler: WorkHandler,
  private val viewModel: GenreViewModel,
) : LibraryScreen,
  MenuItemSelectedListener<Genre> {
  private var viewHolder: LibraryViewHolder? = null
  private var onGenrePressedListener: OnGenrePressed? = null

  fun setOnGenrePressedListener(onGenrePressedListener: OnGenrePressed? = null) {
    this.onGenrePressedListener = onGenrePressedListener
  }

  override fun bind(viewHolder: LibraryViewHolder) {
    this.viewHolder = viewHolder
    viewHolder.setup(R.string.albums_list_empty, adapter)
    adapter.setMenuItemSelectedListener(this)
  }

  override fun observe(viewLifecycleOwner: LifecycleOwner) {
    viewModel.genres.nonNullObserver(viewLifecycleOwner) {
      adapter.submitList(it)
      viewHolder?.refreshingComplete(it.isEmpty())
    }
  }

  override fun onMenuItemSelected(action: Queue, item: Genre) {
    if (action == Default) {
      onItemClicked(item)
      return
    }
    workHandler.queue(item.id, Meta.Genre, action)
  }

  override fun onItemClicked(item: Genre) {
    onGenrePressedListener?.invoke(item)
  }
}
