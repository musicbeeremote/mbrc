package com.kelsos.mbrc.ui.navigation.nowplaying

import com.kelsos.mbrc.data.dao.NowPlaying
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter
import com.raizlabs.android.dbflow.list.FlowCursorList

interface NowPlayingView : BaseView {
  fun update(cursor: FlowCursorList<NowPlaying>)
  fun reload()
  fun trackChanged(trackInfo: TrackInfo)
  fun failure(throwable: Throwable)
}

interface NowPlayingPresenter : Presenter<NowPlayingView> {
  fun reload()
  fun play(position: Int)
  fun moveTrack(from: Int, to: Int)
  fun removeTrack(position: Int)
  fun load()
  fun search(query: String)
}
