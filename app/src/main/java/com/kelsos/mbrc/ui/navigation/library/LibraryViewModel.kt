package com.kelsos.mbrc.ui.navigation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.content.sync.LibrarySyncUseCase
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.launch

class LibraryViewModel(
  private val dispatchers: AppCoroutineDispatchers,
  private val settingsManager: SettingsManager,
  private val librarySyncUseCase: LibrarySyncUseCase,
) : ViewModel() {

  fun refresh() {
    viewModelScope.launch(dispatchers.network) {
      librarySyncUseCase.sync()
    }
  }

  fun setArtistPreference(albumArtistOnly: Boolean) {
    settingsManager.setShouldDisplayOnlyAlbumArtist(albumArtistOnly)
  }
}
