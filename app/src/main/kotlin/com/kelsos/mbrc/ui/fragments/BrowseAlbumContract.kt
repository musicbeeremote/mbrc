package com.kelsos.mbrc.ui.fragments

import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.presenters.Presenter
import com.kelsos.mbrc.views.BaseView
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.config.Module

interface BrowseAlbumView : BaseView {
  fun update(cursor: FlowCursorList<Album>)
}

interface BrowseAlbumPresenter : Presenter<BrowseAlbumView> {
  fun load()
}

class BrowseAlbumModule: Module() {
  init {
    bind(BrowseAlbumPresenter::class.java).to(BrowseAlbumPresenterImpl::class.java).singletonInScope()
  }
}
