package com.kelsos.mbrc.features.library.repositories

import androidx.paging.DataSource
import com.kelsos.mbrc.features.library.data.DataModel
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.interfaces.data.Repository

interface ArtistRepository : Repository<Artist> {
  fun getArtistByGenre(genre: String): DataSource.Factory<Int, Artist>
  fun getAlbumArtistsOnly(): DataSource.Factory<Int, Artist>
  fun allArtists(): DataModel<Artist>
  fun albumArtists(): DataModel<Artist>
}