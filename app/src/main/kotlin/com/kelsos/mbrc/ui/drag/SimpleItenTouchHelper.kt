package com.kelsos.mbrc.ui.drag

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class SimpleItenTouchHelper(private val adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

  override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
    val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
    val swipeFlags = ItemTouchHelper.END
    return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
  }

  override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
    return adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
  }

  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    adapter.onItemDismiss(viewHolder.adapterPosition)
  }

  override fun isLongPressDragEnabled(): Boolean {
    return true
  }

  override fun isItemViewSwipeEnabled(): Boolean {
    return true
  }
}
