package com.kelsos.mbrc.ui.navigation.library

import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface LibraryView : BaseView {
  fun refreshFailed()
  fun showRefreshing()
  fun hideRefreshing()
  fun updateArtistOnlyPreference(albumArtistOnly: Boolean?)
}

interface LibraryPresenter : Presenter<LibraryView> {
  fun refresh()
  fun loadArtistPreference()
  fun setArtistPreference(albumArtistOnly: Boolean)
  fun search(keyword: String)
}
