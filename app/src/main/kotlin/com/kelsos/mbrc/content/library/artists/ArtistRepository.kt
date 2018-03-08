package com.kelsos.mbrc.content.library.artists

import android.arch.paging.DataSource
import com.kelsos.mbrc.interfaces.data.Repository
import io.reactivex.Single

interface ArtistRepository : Repository<ArtistEntity> {
  fun getArtistByGenre(genre: String): Single<DataSource.Factory<Int, ArtistEntity>>
  fun getAlbumArtistsOnly(): Single<DataSource.Factory<Int, ArtistEntity>>
  fun getAllRemoteAndShowAlbumArtist(): Single<DataSource.Factory<Int, ArtistEntity>>
  fun allArtists(): Single<Artists>
  fun albumArtists(): Single<Artists>
}