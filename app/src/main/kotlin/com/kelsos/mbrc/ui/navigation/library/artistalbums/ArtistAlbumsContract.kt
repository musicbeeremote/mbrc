package com.kelsos.mbrc.ui.navigation.library.artistalbums

import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.nowplaying.queue.Queue
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface ArtistAlbumsView : BaseView {
  fun update(albums: List<Album>)
  fun queue(success: Boolean, tracks: Int)
}

interface ArtistAlbumsPresenter : Presenter<ArtistAlbumsView> {
  fun load(artist: String)
  fun queue(@Queue.Action action: String, album: Album)
}
