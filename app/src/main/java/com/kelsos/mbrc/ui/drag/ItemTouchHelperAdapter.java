package com.kelsos.mbrc.ui.drag;

public interface ItemTouchHelperAdapter {
  boolean onItemMove(int from, int to);

  void onItemDismiss(int position);
}
