package com.kelsos.mbrc.ui.activities.profile.album

import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.domain.AlbumInfo
import com.kelsos.mbrc.presenters.Presenter
import com.kelsos.mbrc.views.BaseView
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.config.Module

interface AlbumTracksView : BaseView {
  fun update(cursor: FlowCursorList<Track>)
}

interface AlbumTracksPresenter : Presenter<AlbumTracksView> {
  fun load(album: AlbumInfo)
}

class AlbumTracksModule : Module() {
  init {
    bind(AlbumTracksPresenter::class.java).to(AlbumTracksPresenterImpl::class.java).singletonInScope()
  }
}

