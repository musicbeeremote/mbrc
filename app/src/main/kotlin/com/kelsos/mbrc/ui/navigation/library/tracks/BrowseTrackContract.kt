package com.kelsos.mbrc.ui.navigation.library.tracks

import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface BrowseTrackView : BaseView {
  fun update(list: List<TrackEntity>)

  fun failure(it: Throwable)

  fun hideLoading()
}

interface BrowseTrackPresenter : Presenter<BrowseTrackView> {
  fun load()
  fun reload()
}
