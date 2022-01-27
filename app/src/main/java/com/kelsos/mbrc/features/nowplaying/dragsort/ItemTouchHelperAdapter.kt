package com.kelsos.mbrc.features.nowplaying.dragsort

interface ItemTouchHelperAdapter {
  fun onItemMove(
    from: Int,
    to: Int,
  ): Boolean

  fun onItemDismiss(position: Int)
}
