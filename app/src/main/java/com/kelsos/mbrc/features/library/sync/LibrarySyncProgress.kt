package com.kelsos.mbrc.features.library.sync

data class LibrarySyncProgress(
  val current: Int,
  val total: Int,
  val category: Int,
  val running: Boolean
) {
  companion object {
    fun empty(): LibrarySyncProgress = LibrarySyncProgress(0, 0, 0, false)
  }
}
