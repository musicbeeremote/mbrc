package com.kelsos.mbrc.feature.library

import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.mvvm.BaseViewModel
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.networking.protocol.usecases.UserActionUseCase
import com.kelsos.mbrc.core.networking.protocol.usecases.playAllLibrary
import com.kelsos.mbrc.feature.library.domain.LibrarySyncProgress
import com.kelsos.mbrc.feature.library.domain.LibrarySyncUseCase
import com.kelsos.mbrc.feature.library.domain.LibrarySyncWorkHandler
import com.kelsos.mbrc.feature.library.domain.SyncOutcome
import com.kelsos.mbrc.feature.library.ui.LibraryUiEvent
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber

class LibraryViewModel(
  private val searchModel: LibrarySearchModel,
  private val librarySyncWorkHandler: LibrarySyncWorkHandler,
  private val librarySyncUseCase: LibrarySyncUseCase,
  private val librarySettings: LibrarySettings,
  private val connectionStateFlow: ConnectionStateFlow,
  private val userActionUseCase: UserActionUseCase,
  private val dispatchers: AppCoroutineDispatchers
) : BaseViewModel<LibraryUiEvent>() {
  // Cache flows to prevent creating new instances on every access
  val progress: Flow<LibrarySyncProgress> = librarySyncWorkHandler.syncProgress()

  val syncResults: Flow<SyncOutcome> = librarySyncWorkHandler.syncResults()

  val albumArtistsOnly: Flow<Boolean> = librarySettings.shouldDisplayOnlyArtists

  fun search(string: String = "") {
    viewModelScope.launch {
      searchModel.setTerm(string)
    }
  }

  fun sync() {
    viewModelScope.launch {
      if (!connectionStateFlow.isConnected) {
        emit(LibraryUiEvent.NetworkUnavailable)
        return@launch
      }
      librarySyncWorkHandler.sync()
    }
  }

  fun updateAlbumArtistOnly(bool: Boolean) {
    viewModelScope.launch {
      librarySettings.setShouldDisplayOnlyAlbumArtist(bool)
    }
  }

  fun displayLibraryStats() {
    viewModelScope.launch {
      val syncStats = librarySyncUseCase.syncStats()
      emit(LibraryUiEvent.LibraryStatsReady(syncStats))
    }
  }

  fun playAll(shuffle: Boolean) {
    viewModelScope.launch(dispatchers.network) {
      if (!connectionStateFlow.isConnected) {
        emit(LibraryUiEvent.NetworkUnavailable)
        return@launch
      }
      try {
        userActionUseCase.playAllLibrary(shuffle)
        emit(LibraryUiEvent.PlayAllSuccess)
      } catch (e: IOException) {
        Timber.e(e)
        emit(LibraryUiEvent.PlayAllFailed)
      }
    }
  }
}
