package com.kelsos.mbrc.ui.navigation.library.tracks

import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface BrowseTrackView : BaseView {
  fun update(pagedList: PagedList<TrackEntity>)

  fun updateIndexes(indexes: List<String>)

  fun failure(it: Throwable)

  fun hideLoading()
}

interface BrowseTrackPresenter : Presenter<BrowseTrackView> {
  fun load()
  fun reload()
}