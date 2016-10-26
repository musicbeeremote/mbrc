package com.kelsos.mbrc.ui.fragments

import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.mvp.Presenter
import com.kelsos.mbrc.mvp.BaseView
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.config.Module

interface BrowseArtistView: BaseView {
  fun update(data: FlowCursorList<Artist>)
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
