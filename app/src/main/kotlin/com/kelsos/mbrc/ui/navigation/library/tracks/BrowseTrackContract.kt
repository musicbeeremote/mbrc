package com.kelsos.mbrc.ui.navigation.library.tracks

import androidx.paging.PagingData
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface BrowseTrackView : BaseView {
  suspend fun update(tracks: PagingData<Track>)
  fun search(term: String)
  fun queue(success: Boolean, tracks: Int)
  fun hideLoading()
}

interface BrowseTrackPresenter : Presenter<BrowseTrackView> {
  fun load()
  fun sync()
  fun queue(track: Track, @LibraryPopup.Action action: String? = null)
}
