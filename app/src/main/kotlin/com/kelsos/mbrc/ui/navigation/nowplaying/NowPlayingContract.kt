package com.kelsos.mbrc.ui.navigation.nowplaying

import androidx.paging.PagingData
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.content.nowplaying.NowPlaying
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface NowPlayingView : BaseView {
  suspend fun update(data: PagingData<NowPlaying>)
  fun trackChanged(track: PlayingTrack, scrollToTrack: Boolean = false)
  fun failure(throwable: Throwable)
  fun loading(show: Boolean = false)
}

interface NowPlayingPresenter : Presenter<NowPlayingView> {
  fun reload(scrollToTrack: Boolean)
  fun play(position: Int)
  fun moveTrack(from: Int, to: Int)
  fun removeTrack(position: Int)
  fun load()
  fun search(query: String)
}
