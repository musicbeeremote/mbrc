package com.kelsos.mbrc.ui

import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.features.library.OnFastScrollListener
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller.BubbleTextGetter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import timber.log.Timber

abstract class FastScrollableAdapter<T, VH : BindableViewHolder<T>>(
  diffCallback: DiffUtil.ItemCallback<T>
) : PagedListAdapter<T, VH>(diffCallback), BubbleTextGetter, OnFastScrollListener {

  private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Main)
  private var deferred: Deferred<Unit>? = null
  private var indexes: List<String> = emptyList()

  private var listener: MenuItemSelectedListener<T>? = null

  protected var fastScrolling: Boolean = false
    private set

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener<T>) {
    this.listener = listener
  }

  protected fun requireListener(): MenuItemSelectedListener<T> {
    return checkNotNull(listener) { "listener was null" }
  }

  override fun submitList(pagedList: PagedList<T>?) {
    Timber.v("submit")
    deferred?.cancel()
    super.submitList(pagedList)
  }

  override fun getTextToShowInBubble(pos: Int): String {
    return if (pos < indexes.size) indexes[pos] else "-"
  }

  override fun onStart(firstVisibleItemPosition: Int, lastVisibleItemPosition: Int) {
    deferred?.cancel()
    fastScrolling = true
    notifyItemRangeChanged(firstVisibleItemPosition, lastVisibleItemPosition)
  }

  override fun onComplete(firstVisibleItemPosition: Int, lastVisibleItemPosition: Int) {
    Timber.v("notify")
    deferred?.cancel()
    deferred = scope.async {
      delay(400)
      Timber.v("notifying")
      notifyItemRangeChanged(firstVisibleItemPosition, lastVisibleItemPosition)
      fastScrolling = false
    }
  }

  fun setIndexes(indexes: List<String>) {
    this.indexes = indexes
  }
}