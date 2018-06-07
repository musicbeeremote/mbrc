package com.kelsos.mbrc.content.library.artists

import androidx.paging.DataSource
import com.kelsos.mbrc.content.library.DataModel
import com.kelsos.mbrc.interfaces.data.Repository
import io.reactivex.Single

interface ArtistRepository : Repository<ArtistEntity> {
  fun getArtistByGenre(genre: String): Single<DataSource.Factory<Int, ArtistEntity>>
  fun getAlbumArtistsOnly(): Single<DataSource.Factory<Int, ArtistEntity>>
  fun getAllRemoteAndShowAlbumArtist(): Single<DataSource.Factory<Int, ArtistEntity>>
  fun allArtists(): Single<DataModel<ArtistEntity>>
  fun albumArtists(): Single<DataModel<ArtistEntity>>
}