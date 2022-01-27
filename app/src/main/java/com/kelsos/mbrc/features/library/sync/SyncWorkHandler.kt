package com.kelsos.mbrc.features.library.sync

import kotlinx.coroutines.flow.Flow

interface SyncWorkHandler {
  fun sync(auto: Boolean = false)

  fun syncProgress(): Flow<LibrarySyncProgress>
}
