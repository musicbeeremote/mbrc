package com.kelsos.mbrc.content.library.artists

import android.arch.lifecycle.LiveData
import com.kelsos.mbrc.interfaces.data.Repository
import io.reactivex.Single

interface ArtistRepository : Repository<ArtistEntity> {
  fun getArtistByGenre(genre: String): Single<LiveData<List<ArtistEntity>>>
  fun getAlbumArtistsOnly(): Single<LiveData<List<ArtistEntity>>>
  fun getAllRemoteAndShowAlbumArtist(): Single<LiveData<List<ArtistEntity>>>
}
