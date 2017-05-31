package com.kelsos.mbrc.ui.navigation.library.albums

import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.now_playing.queue.Queue
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter
import com.raizlabs.android.dbflow.list.FlowCursorList

interface BrowseAlbumView : BaseView {
  fun update(cursor: FlowCursorList<Album>)
  fun search(term: String)
  fun queue(success: Boolean, tracks: Int)
  fun hideLoading()
  fun showLoading()
}

interface BrowseAlbumPresenter : Presenter<BrowseAlbumView> {
  fun load()
  fun sync()
  fun queue(@Queue.Action action: String, entry: Album)
}
