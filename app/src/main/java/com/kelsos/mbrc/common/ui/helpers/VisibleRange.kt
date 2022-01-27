package com.kelsos.mbrc.common.ui.helpers

data class VisibleRange(
  val firstItem: Int,
  val lastItem: Int,
) {
  val itemCount: Int
    get() = lastItem - firstItem
}
