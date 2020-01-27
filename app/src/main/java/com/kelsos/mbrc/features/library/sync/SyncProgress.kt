package com.kelsos.mbrc.features.library.sync

import com.kelsos.mbrc.common.Meta

data class SyncProgress(
  val current: Int,
  val total: Int,
  @Meta.Type val type: Int
) {

  override fun toString(): String {
    return "$current of $total -- ($type)"
  }
}