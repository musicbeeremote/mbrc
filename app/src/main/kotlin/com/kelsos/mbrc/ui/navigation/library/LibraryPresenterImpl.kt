package com.kelsos.mbrc.ui.navigation.library

import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.ui.navigation.library.LibrarySyncInteractor.OnCompleteListener
import com.kelsos.mbrc.utilities.SettingsManager
import kotlinx.coroutines.launch
import javax.inject.Inject

class LibraryPresenterImpl
@Inject
constructor(
  private val settingsManager: SettingsManager,
  private val bus: RxBus,
  private val librarySyncInteractor: LibrarySyncInteractor,
  private val searchModel: LibrarySearchModel
) : LibraryPresenter,
  OnCompleteListener,
  BasePresenter<LibraryView>(),
  LibrarySyncInteractor.OnStartListener {

  override fun refresh() {
    view?.showRefreshing()
    scope.launch {
      librarySyncInteractor.sync()
    }
  }

  override fun attach(view: LibraryView) {
    super.attach(view)
    librarySyncInteractor.setOnCompleteListener(this)
    librarySyncInteractor.setOnStartListener(this)
    if (!librarySyncInteractor.isRunning()) {
      view.hideRefreshing()
    }
  }

  override fun detach() {
    super.detach()
    librarySyncInteractor.setOnCompleteListener(null)
    librarySyncInteractor.setOnStartListener(null)
  }

  override fun onTermination() {
    scope.launch {
      view?.hideRefreshing()
    }
  }

  override fun onFailure(throwable: Throwable) {
    scope.launch {
      view?.refreshFailed()
    }
  }

  override fun loadArtistPreference() {
    scope.launch {
      val shouldDisplay = settingsManager.shouldDisplayOnlyAlbumArtists()
      view?.updateArtistOnlyPreference(shouldDisplay)
    }
  }

  override fun setArtistPreference(albumArtistOnly: Boolean) {
    settingsManager.setShouldDisplayOnlyAlbumArtist(albumArtistOnly)
    bus.post(ArtistTabRefreshEvent())
  }

  override fun search(keyword: String) {
    searchModel.search(keyword)
  }

  override fun onSuccess(stats: LibraryStats) {
    view?.syncComplete(stats)
  }

  override fun onStart() {
    scope.launch {
      view?.showRefreshing()
    }
  }
}

