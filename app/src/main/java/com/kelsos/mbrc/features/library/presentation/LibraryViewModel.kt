package com.kelsos.mbrc.features.library.presentation

import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.sync.SyncResult
import com.kelsos.mbrc.features.library.sync.SyncWorkHandler
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.launch

class LibraryViewModel(
  dispatchers: AppCoroutineDispatchers,
  private val searchModel: LibrarySearchModel,
  private val settingsManager: SettingsManager,
  private val syncWorkHandler: SyncWorkHandler
) : BaseViewModel<SyncResult>(dispatchers) {

  val syncProgress = syncWorkHandler.syncProgress()
  val albumArtistOnly get() = settingsManager.onlyAlbumArtists().value

  fun refresh() {
    scope.launch {
      syncWorkHandler.sync()
    }
  }

  fun search(search: String = "") {
    searchModel.search(search)
  }

  fun setAlbumArtistOnly(checked: Boolean) {
    settingsManager.setShouldDisplayOnlyAlbumArtist(checked)
  }
}
