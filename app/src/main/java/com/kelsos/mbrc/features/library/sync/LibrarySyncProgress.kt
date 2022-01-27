package com.kelsos.mbrc.features.library.sync

data class LibrarySyncProgress(
  val current: Int,
  val total: Int,
  val category: Int,
  val running: Boolean,
) {
  fun float(): Float {
    if (total == 0) {
      return 0.0f
    }
    return current.toFloat().div(total)
  }

  companion object {
    fun empty(): LibrarySyncProgress = LibrarySyncProgress(0, 0, 0, false)
  }
}
