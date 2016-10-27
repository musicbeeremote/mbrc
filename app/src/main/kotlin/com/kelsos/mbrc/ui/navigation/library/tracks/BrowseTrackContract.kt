package com.kelsos.mbrc.ui.navigation.library.tracks

import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter
import com.raizlabs.android.dbflow.list.FlowCursorList

interface BrowseTrackView : BaseView {
  fun update(it: FlowCursorList<Track>)
  fun failure(it: Throwable)
}

interface BrowseTrackPresenter : Presenter<BrowseTrackView> {
  fun load()
  fun reload()
}
