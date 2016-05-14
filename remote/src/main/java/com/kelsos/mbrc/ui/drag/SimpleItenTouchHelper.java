package com.kelsos.mbrc.ui.drag;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SimpleItenTouchHelper extends ItemTouchHelper.Callback {
  private final ItemTouchHelperAdapter adapter;

  public SimpleItenTouchHelper(ItemTouchHelperAdapter adapter) {
    this.adapter = adapter;
  }

  @Override public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    int swipeFlags = ItemTouchHelper.END;
    return makeMovementFlags(dragFlags, swipeFlags);
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
    return adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
  }

  @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    adapter.onItemDismiss(viewHolder.getAdapterPosition());
  }

  @Override public boolean isLongPressDragEnabled() {
    return true;
  }

  @Override public boolean isItemViewSwipeEnabled() {
    return true;
  }
}
