package com.kelsos.mbrc.ui.navigation.library

import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface LibraryView : BaseView {
  fun refreshFailed()
  fun showRefreshing()
  fun hideRefreshing()
  fun updateArtistOnlyPreference(albumArtistOnly: Boolean?)
  fun updateSyncProgress(progress: SyncProgress)
}

interface LibraryPresenter : Presenter<LibraryView> {
  fun refresh()
  fun loadArtistPreference()
  fun setArtistPreference(albumArtistOnly: Boolean)
}