package com.kelsos.mbrc.ui.navigation.library

import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.ui.navigation.library.LibrarySyncInteractor.OnCompleteListener
import com.kelsos.mbrc.utilities.SettingsManager
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

class LibraryPresenterImpl
@Inject constructor(
    @Named("io") private val ioScheduler: Scheduler,
    @Named("main") private val mainScheduler: Scheduler,
    private val settingsManager: SettingsManager,
    private val bus: RxBus,
    private val librarySyncInteractor: LibrarySyncInteractor
) : LibraryPresenter, OnCompleteListener, BasePresenter<LibraryView>() {

  override fun refresh() {
    view?.showRefreshing()
    librarySyncInteractor.sync()
  }

  override fun attach(view: LibraryView) {
    super.attach(view)
    librarySyncInteractor.setOnCompleteListener(this)
    if (!librarySyncInteractor.isRunning()) {
      view.hideRefreshing()
    }
  }

  override fun detach() {
    super.detach()
    librarySyncInteractor.setOnCompleteListener(null)
  }

  override fun onTermination() {
    view?.hideRefreshing()
  }

  override fun onFailure(throwable: Throwable) {
    view?.refreshFailed()
  }

  override fun loadArtistPreference() {
    settingsManager.shouldDisplayOnlyAlbumArtists()
        .subscribeOn(ioScheduler)
        .observeOn(mainScheduler)
        .subscribe({
          view?.updateArtistOnlyPreference(it)
        }, {

        })
  }

  override fun setArtistPreference(albumArtistOnly: Boolean) {
    settingsManager.setShouldDisplayOnlyAlbumArtist(albumArtistOnly)
    bus.post(ArtistTabRefreshEvent())
  }

  override fun onSuccess() {
    //todo show success message
  }
}

