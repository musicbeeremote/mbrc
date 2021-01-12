package com.kelsos.mbrc.features.library.presentation

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.BindableViewHolder

abstract class LibraryAdapter<T : Any, VH : BindableViewHolder<T>>(
  diffCallback: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, VH>(diffCallback) {

  private var listener: MenuItemSelectedListener<T>? = null

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener<T>) {
    this.listener = listener
  }

  protected fun requireListener(): MenuItemSelectedListener<T> {
    return checkNotNull(listener) { "listener was null" }
  }
}
