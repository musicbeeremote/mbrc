package com.kelsos.mbrc.features.library.repositories

import androidx.paging.PagingData
import com.kelsos.mbrc.common.data.Repository
import com.kelsos.mbrc.features.library.data.Artist
import kotlinx.coroutines.flow.Flow

interface ArtistRepository : Repository<Artist> {
  fun getArtistByGenre(genreId: Long): Flow<PagingData<Artist>>

  fun getAlbumArtistsOnly(): Flow<PagingData<Artist>>
}
