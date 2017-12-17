package com.kelsos.mbrc.content.library.artists

import androidx.paging.DataSource
import com.kelsos.mbrc.interfaces.data.Repository

interface ArtistRepository : Repository<Artist> {
  suspend fun getArtistByGenre(genre: String): DataSource.Factory<Int, Artist>
  suspend fun getAlbumArtistsOnly(): DataSource.Factory<Int, Artist>
  suspend fun getAllRemoteAndShowAlbumArtist(): DataSource.Factory<Int, Artist>
}
