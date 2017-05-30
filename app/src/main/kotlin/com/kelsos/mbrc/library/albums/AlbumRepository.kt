package com.kelsos.mbrc.library.albums

import com.kelsos.mbrc.repository.Repository
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Single

interface AlbumRepository : Repository<Album> {
  fun getAlbumsByArtist(artist: String): Single<FlowCursorList<Album>>
}
