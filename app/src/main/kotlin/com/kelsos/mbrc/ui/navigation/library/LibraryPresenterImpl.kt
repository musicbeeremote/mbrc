package com.kelsos.mbrc.ui.navigation.library

import com.kelsos.mbrc.content.sync.LibrarySyncInteractor
import com.kelsos.mbrc.content.sync.LibrarySyncInteractor.OnCompleteListener
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.utilities.AppRxSchedulers
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

@LibraryFragment.Presenter
class LibraryPresenterImpl
@Inject constructor(
  private val appRxSchedulers: AppRxSchedulers,
  private val settingsManager: SettingsManager,
  private val librarySyncInteractor: LibrarySyncInteractor,
  syncProgressProvider: SyncProgressProvider
) : LibraryPresenter, OnCompleteListener, BasePresenter<LibraryView>() {

  init {
//    syncProgressProvider.observe(this, Observer {
//      if (it == null) {
//        return@Observer
//      }
//
//      Timber.v("progress $it")
//      view().updateSyncProgress(it)
//    })
  }

  override fun refresh() {
    view().showRefreshing()
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
    view().hideRefreshing()
  }

  override fun onFailure(throwable: Throwable) {
    view().refreshFailed()
  }

  override fun loadArtistPreference() {
    disposables += settingsManager.shouldDisplayOnlyAlbumArtists()
      .subscribeOn(appRxSchedulers.disk)
      .observeOn(appRxSchedulers.main)
      .subscribe({
        view().updateArtistOnlyPreference(it)
      }, {
      })
  }

  override fun setArtistPreference(albumArtistOnly: Boolean) {
    settingsManager.setShouldDisplayOnlyAlbumArtist(albumArtistOnly)
    //bus.post(ArtistTabRefreshEvent())
  }

  override fun onSuccess() {
    //todo show success message
  }
}