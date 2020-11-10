package com.kelsos.mbrc.features.library.repositories

import androidx.paging.DataSource
import com.kelsos.mbrc.common.data.Repository
import com.kelsos.mbrc.features.library.data.Artist

interface ArtistRepository : Repository<Artist> {
  fun getArtistByGenre(genre: String): DataSource.Factory<Int, Artist>
  fun getAlbumArtistsOnly(): DataSource.Factory<Int, Artist>
  fun allArtists(): DataSource.Factory<Int, Artist>
  fun albumArtists(): DataSource.Factory<Int, Artist>
}