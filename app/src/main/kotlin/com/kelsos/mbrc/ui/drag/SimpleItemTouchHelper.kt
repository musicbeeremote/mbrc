package com.kelsos.mbrc.ui.drag

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class SimpleItemTouchHelper(private val adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

  override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
    val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
    val swipeFlags = ItemTouchHelper.END
    return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
  }

  override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
    if (viewHolder.itemViewType != target.itemViewType) {
      return false;
    }

    // Notify the adapter of the move
    adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition);
    return true;
  }

  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    adapter.onItemDismiss(viewHolder.adapterPosition)
  }

  override fun isLongPressDragEnabled(): Boolean {
    return false
  }

  override fun isItemViewSwipeEnabled(): Boolean {
    return true
  }
}
