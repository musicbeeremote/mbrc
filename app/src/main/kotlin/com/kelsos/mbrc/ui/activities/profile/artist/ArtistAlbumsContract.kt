package com.kelsos.mbrc.ui.activities.profile.artist

import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.presenters.Presenter
import com.kelsos.mbrc.views.BaseView
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.config.Module

interface ArtistAlbumsView : BaseView {
  fun update(albums: FlowCursorList<Album>)
}

interface ArtistAlbumsPresenter: Presenter<ArtistAlbumsView> {
  fun load(artist: String)
}

class ArtistAlbumsModule : Module() {
  init {
    bind(ArtistAlbumsPresenter::class.java).to(ArtistAlbumsPresenterImpl::class.java).singletonInScope()
  }
}
