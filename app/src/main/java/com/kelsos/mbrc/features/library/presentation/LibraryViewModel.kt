package com.kelsos.mbrc.features.library.presentation

import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.sync.SyncResult
import com.kelsos.mbrc.features.library.sync.SyncStatProvider
import com.kelsos.mbrc.features.library.sync.SyncWorkHandler
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.settings.SettingsManager
import com.kelsos.mbrc.features.work.WorkHandler
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.launch

class LibraryViewModel(
  private val dispatchers: AppCoroutineDispatchers,
  private val searchModel: LibrarySearchModel,
  private val settingsManager: SettingsManager,
  private val syncWorkHandler: SyncWorkHandler,
  private val queueWorkHandler: WorkHandler,
  private val syncStatProvider: SyncStatProvider
) : BaseViewModel<SyncResult>() {

  val syncProgress = syncWorkHandler.syncProgress()
  val albumArtistOnly get() = settingsManager.onlyAlbumArtists()
  val syncState = syncStatProvider.stats

  fun refresh() {
    viewModelScope.launch(dispatchers.network) {
      syncWorkHandler.sync()
    }
  }

  fun search(search: String = "") {
    searchModel.search(search)
  }

  fun setAlbumArtistOnly(checked: Boolean) {
    viewModelScope.launch {
      settingsManager.setShouldDisplayOnlyAlbumArtist(checked)
    }
  }

  fun updateStats() {
    syncStatProvider.update()
  }

  fun queue(id: Long, meta: Meta, action: Queue) {
    queueWorkHandler.queue(id, meta, action)
  }
}
