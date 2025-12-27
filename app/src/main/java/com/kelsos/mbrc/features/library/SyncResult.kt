package com.kelsos.mbrc.features.library

sealed class SyncResult {
  data class Success(val stats: LibraryStats) : SyncResult()

  data object Noop : SyncResult()

  data class Failed(val message: String) : SyncResult()
}
