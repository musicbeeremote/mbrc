package com.kelsos.mbrc.ui

import android.arch.paging.PagedList
import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.OnFastScrollListener
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller.BubbleTextGetter
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import timber.log.Timber

abstract class FastScrollableAdapter<T, VH : BindableViewHolder<T>>(
  diffCallback: DiffUtil.ItemCallback<T>
) : PagedListAdapter<T, VH>(diffCallback), BubbleTextGetter, OnFastScrollListener {

  private var indexes: List<String> = emptyList()

  private var listener: MenuItemSelectedListener<T>? = null

  protected var fastScrolling: Boolean = false
    private set

  private var fastScrollNotify: Deferred<Unit>? = null

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener<T>) {
    this.listener = listener
  }

  protected fun requireListener(): MenuItemSelectedListener<T> {
    return checkNotNull(listener) { "listener was null" }
  }

  override fun submitList(pagedList: PagedList<T>) {
    fastScrollNotify?.cancel()
    super.submitList(pagedList)
  }

  override fun getTextToShowInBubble(pos: Int): String {
    return if (pos < indexes.size) indexes[pos] else "-"
  }

  override fun onStart() {
    fastScrolling = true
  }

  override fun onComplete(firstVisibleItemPosition: Int, lastVisibleItemPosition: Int) {
    fastScrolling = false
    Timber.v("scrolling done")
    fastScrollNotify = async(UI) {
      delay(400)
      notifyItemRangeChanged(firstVisibleItemPosition, lastVisibleItemPosition)
    }
  }

  fun setIndexes(indexes: List<String>) {
    this.indexes = indexes
  }
}