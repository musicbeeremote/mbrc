package com.kelsos.mbrc.features.library.sync

data class LibrarySyncProgress(
  val current: Int,
  val total: Int,
  val category: Int,
  val running: Boolean
)