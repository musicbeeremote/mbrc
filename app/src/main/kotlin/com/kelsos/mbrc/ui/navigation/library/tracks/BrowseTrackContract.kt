package com.kelsos.mbrc.ui.navigation.library.tracks

import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.nowplaying.queue.Queue
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface BrowseTrackView : BaseView {
  fun update(it: List<Track>)
  fun search(term: String)
  fun queue(success: Boolean, tracks: Int)
  fun hideLoading()
  fun showLoading()
}

interface BrowseTrackPresenter : Presenter<BrowseTrackView> {
  fun load()
  fun sync()
  fun queue(track: Track, @Queue.Action action: String? = null)
}
