package com.kelsos.mbrc.ui.navigation.library.artistalbums

import androidx.paging.PagingData
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface ArtistAlbumsView : BaseView {
  suspend fun update(albums: PagingData<Album>)
  fun queue(success: Boolean, tracks: Int)
}

interface ArtistAlbumsPresenter : Presenter<ArtistAlbumsView> {
  fun load(artist: String)
  fun queue(@LibraryPopup.Action action: String, album: Album)
}
