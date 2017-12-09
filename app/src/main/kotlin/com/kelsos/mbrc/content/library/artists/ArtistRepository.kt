package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.interfaces.data.Repository
import io.reactivex.Single

interface ArtistRepository : Repository<Artist> {
  fun getArtistByGenre(genre: String): Single<List<Artist>>
  fun getAlbumArtistsOnly(): Single<List<Artist>>
  fun getAllRemoteAndShowAlbumArtist(): Single<List<Artist>>
}
