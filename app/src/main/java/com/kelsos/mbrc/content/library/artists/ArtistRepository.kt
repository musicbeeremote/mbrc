package com.kelsos.mbrc.content.library.artists

import androidx.paging.DataSource
import com.kelsos.mbrc.content.library.DataModel
import com.kelsos.mbrc.interfaces.data.Repository

interface ArtistRepository : Repository<ArtistEntity> {
  suspend fun getArtistByGenre(genre: String): DataSource.Factory<Int, ArtistEntity>
  suspend fun getAlbumArtistsOnly(): DataSource.Factory<Int, ArtistEntity>
  suspend fun allArtists(): DataModel<ArtistEntity>
  suspend fun albumArtists(): DataModel<ArtistEntity>
}