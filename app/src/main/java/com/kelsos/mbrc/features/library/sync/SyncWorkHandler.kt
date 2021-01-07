package com.kelsos.mbrc.features.library.sync

import androidx.lifecycle.LiveData

interface SyncWorkHandler {
  fun sync(auto: Boolean = false)
  fun syncProgress(): LiveData<LibrarySyncProgress>
}
