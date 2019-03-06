package com.kelsos.mbrc.ui.navigation.library

import androidx.lifecycle.ViewModel
import com.kelsos.mbrc.content.sync.LibrarySyncUseCase
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LibraryViewModel(
  private val dispatchers: AppCoroutineDispatchers,
  private val settingsManager: SettingsManager,
  private val librarySyncUseCase: LibrarySyncUseCase,
  val syncProgress: SyncProgressProvider
) : ViewModel() {

  private val viewModelJob: Job = Job()
  private val networkScope = CoroutineScope(dispatchers.network + viewModelJob)

  val displayOnlyAlbumArtists = settingsManager.shouldDisplayOnlyAlbumArtists()

  fun refresh() {
    networkScope.launch {
      val syncResult = librarySyncUseCase.sync()
    }
  }

  fun setArtistPreference(albumArtistOnly: Boolean) {
    settingsManager.setShouldDisplayOnlyAlbumArtist(albumArtistOnly)
  }

  override fun onCleared() {
    viewModelJob.cancel()
    super.onCleared()
  }
}