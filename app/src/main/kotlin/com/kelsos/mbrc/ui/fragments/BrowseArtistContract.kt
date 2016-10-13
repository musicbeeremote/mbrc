package com.kelsos.mbrc.ui.fragments

import com.kelsos.mbrc.presenters.Presenter
import com.kelsos.mbrc.views.BaseView
import toothpick.config.Module

interface BrowseArtistView: BaseView {

}

interface BrowseArtistPresenter : Presenter<BrowseArtistView> {
  fun load()
  fun reload()
}

class BrowseArtistModule: Module() {
  init {
    bind(BrowseArtistPresenter::class.java)
        .to(BrowseArtistPresenterImpl::class.java)
        .singletonInScope()
  }
}
