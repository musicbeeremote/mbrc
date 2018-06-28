package com.kelsos.mbrc.ui.navigation.library

import com.kelsos.mbrc.content.sync.LibrarySyncUseCase
import com.kelsos.mbrc.content.sync.LibrarySyncUseCase.OnCompleteListener
import com.kelsos.mbrc.metrics.SyncedData
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.preferences.SettingsManager
import kotlinx.coroutines.launch

class LibraryPresenterImpl(
  private val settingsManager: SettingsManager,
  private val syncUseCase: LibrarySyncUseCase,
  private val searchModel: LibrarySearchModel
) : LibraryPresenter,
  OnCompleteListener,
  BasePresenter<LibraryView>(),
  LibrarySyncUseCase.OnStartListener {

  override fun refresh() {
    view().showSyncProgress()
    scope.launch {
      syncUseCase.sync()
    }
  }

  override fun attach(view: LibraryView) {
    super.attach(view)
    syncUseCase.setOnCompleteListener(this)
    syncUseCase.setOnStartListener(this)
    if (!syncUseCase.isRunning()) {
      view.hideSyncProgress()
    }
  }

  override fun detach() {
    super.detach()
    syncUseCase.setOnCompleteListener(null)
    syncUseCase.setOnStartListener(null)
  }

  override fun onTermination() {
    scope.launch {
      view().hideSyncProgress()
    }
  }

  override fun onFailure(throwable: Throwable) {
    scope.launch {
      view().syncFailure()
    }
  }

  override fun showStats() {
    scope.launch {
      view().showStats(syncUseCase.syncStats())
    }
  }

  override fun loadArtistPreference() {
    scope.launch {
      val shouldDisplay = settingsManager.shouldDisplayOnlyAlbumArtists()
      view().updateArtistOnlyPreference(shouldDisplay)
    }
  }

  override fun setArtistPreference(albumArtistOnly: Boolean) {
    settingsManager.setShouldDisplayOnlyAlbumArtist(albumArtistOnly)
  }

  override fun search(keyword: String) {
    searchModel.term.tryEmit(keyword)
  }

  override fun onSuccess(stats: SyncedData) {
    view().syncComplete(stats)
  }

  override fun onStart() {
    scope.launch {
      view().showSyncProgress()
    }
  }
}
