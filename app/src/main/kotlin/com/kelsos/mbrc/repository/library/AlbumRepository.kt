package com.kelsos.mbrc.repository.library

import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.repository.Repository
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Single

interface AlbumRepository : Repository<Album> {
  fun getAlbumsByArtist(artist: String): Single<FlowCursorList<Album>>
}
