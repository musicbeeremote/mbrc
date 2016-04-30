package com.kelsos.mbrc.interfaces

interface ITouchHelperAdapter {

  fun onItemMove(from: Int, to: Int)

  fun onItemDismiss(position: Int)
}
