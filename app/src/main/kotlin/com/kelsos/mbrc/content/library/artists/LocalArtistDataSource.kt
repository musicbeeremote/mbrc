package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.interfaces.data.LocalDataSource
import io.reactivex.Single

interface LocalArtistDataSource : LocalDataSource<Artist> {
  fun getArtistByGenre(genre: String): Single<List<Artist>>
  fun getAlbumArtists(): Single<List<Artist>>
}
