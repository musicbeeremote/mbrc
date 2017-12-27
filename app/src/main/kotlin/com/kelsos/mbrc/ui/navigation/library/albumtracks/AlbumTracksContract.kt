package com.kelsos.mbrc.ui.navigation.library.albumtracks

import androidx.paging.PagingData
import com.kelsos.mbrc.content.library.albums.AlbumInfo
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface AlbumTracksView : BaseView {
  suspend fun update(tracks: PagingData<Track>)
  fun queue(success: Boolean, tracks: Int)
}

interface AlbumTracksPresenter : Presenter<AlbumTracksView> {
  fun load(album: AlbumInfo)
  fun queue(entry: Track, @LibraryPopup.Action action: String? = null)
  fun queueAlbum(artist: String, album: String)
}
