package com.kelsos.mbrc.ui.navigation.library

import androidx.lifecycle.ViewModel
import com.kelsos.mbrc.content.sync.LibrarySyncUseCase
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.experimental.launch

class LibraryViewModel(
  private val dispatchers: AppCoroutineDispatchers,
  private val settingsManager: SettingsManager,
  private val librarySyncUseCase: LibrarySyncUseCase,
  val syncProgress: SyncProgressProvider
) : ViewModel() {

  val displayOnlyAlbumArtists = settingsManager.shouldDisplayOnlyAlbumArtists()

  fun refresh() {
    launch(dispatchers.network) {
      librarySyncUseCase.sync()
    }
  }

  fun setArtistPreference(albumArtistOnly: Boolean) {
    settingsManager.setShouldDisplayOnlyAlbumArtist(albumArtistOnly)
  }

}