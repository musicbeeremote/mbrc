package com.kelsos.mbrc.features.library.presentation

import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.sync.LibrarySyncProgress
import com.kelsos.mbrc.features.library.sync.SyncResult
import com.kelsos.mbrc.features.library.sync.SyncStatProvider
import com.kelsos.mbrc.features.library.sync.SyncWorkHandler
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.settings.SettingsManager
import com.kelsos.mbrc.features.work.WorkHandler
import com.kelsos.mbrc.metrics.SyncedData
import com.kelsos.mbrc.metrics.empty
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LibraryActions(
  private val settingsManager: SettingsManager,
  private val syncWorkHandler: SyncWorkHandler,
  private val queueWorkHandler: WorkHandler,
  private val scope: CoroutineScope,
  private val dispatchers: AppCoroutineDispatchers
) {
  fun refresh() {
    scope.launch(dispatchers.network) {
      syncWorkHandler.sync()
    }
  }

  fun queue(id: Long, meta: Meta, action: Queue) {
    queueWorkHandler.queue(id, meta, action)
  }

  fun setAlbumArtistOnly(checked: Boolean) {
    scope.launch {
      settingsManager.setShouldDisplayOnlyAlbumArtist(checked)
    }
  }
}

data class LibraryState(
  val albumArtistOnly: Boolean = false,
  val syncState: SyncedData = SyncedData.empty(),
  val syncProgress: LibrarySyncProgress = LibrarySyncProgress.empty()
)

class LibraryViewModel(
  dispatchers: AppCoroutineDispatchers,
  private val searchModel: LibrarySearchModel,
  settingsManager: SettingsManager,
  syncWorkHandler: SyncWorkHandler,
  queueWorkHandler: WorkHandler,
  syncStatProvider: SyncStatProvider
) : BaseViewModel<SyncResult>() {

  val state = combine(
    settingsManager.onlyAlbumArtists(),
    syncStatProvider.stats,
    syncWorkHandler.syncProgress().asFlow()
  ) { onlyAlbumArtists: Boolean, syncedData: SyncedData, syncProgress: LibrarySyncProgress ->
    LibraryState(
      albumArtistOnly = onlyAlbumArtists,
      syncState = syncedData,
      syncProgress = syncProgress
    )
  }
  val actions: LibraryActions = LibraryActions(
    settingsManager,
    syncWorkHandler,
    queueWorkHandler,
    viewModelScope,
    dispatchers
  )

  fun search(search: String = "") {
    searchModel.search(search)
  }
}
