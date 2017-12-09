package com.kelsos.mbrc.ui.navigation.library.artists

import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface BrowseArtistView : BaseView {
  fun update(data: List<Artist>)
  fun failure(throwable: Throwable)
  fun hideLoading()
  fun showLoading()
}

interface BrowseArtistPresenter : Presenter<BrowseArtistView> {
  fun load()
  fun reload()
}

