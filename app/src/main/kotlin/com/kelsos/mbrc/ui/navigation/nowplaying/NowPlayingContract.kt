package com.kelsos.mbrc.ui.navigation.nowplaying

import com.kelsos.mbrc.content.library.tracks.TrackInfo
import com.kelsos.mbrc.content.nowplaying.NowPlayingEntity
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface NowPlayingView : BaseView {
  fun update(data: List<NowPlayingEntity>)
  fun trackChanged(trackInfo: TrackInfo, scrollToTrack: Boolean = false)
  fun failure(throwable: Throwable)
  fun showLoading()
  fun hideLoading()
}

interface NowPlayingPresenter : Presenter<NowPlayingView> {
  fun reload(scrollToTrack: Boolean)
  fun play(position: Int)
  fun moveTrack(from: Int, to: Int)
  fun removeTrack(position: Int)
  fun load()
  fun search(query: String)
}