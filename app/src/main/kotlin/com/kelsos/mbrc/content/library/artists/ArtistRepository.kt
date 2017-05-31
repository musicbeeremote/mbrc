package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.interfaces.data.Repository
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Single

interface ArtistRepository : Repository<Artist> {
  fun getArtistByGenre(genre: String): Single<FlowCursorList<Artist>>
  fun getAlbumArtistsOnly(): Single<FlowCursorList<Artist>>
  fun getAllRemoteAndShowAlbumArtist(): Single<FlowCursorList<Artist>>
}
