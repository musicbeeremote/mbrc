package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.library.Album
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Single

interface AlbumRepository : Repository<Album> {
  fun getAlbumsByArtist(artist: String): Single<FlowCursorList<Album>>
}
