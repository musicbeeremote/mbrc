package com.kelsos.mbrc.core.data.library.artist

import androidx.paging.PagingData
import com.kelsos.mbrc.core.data.Repository
import kotlinx.coroutines.flow.Flow

interface ArtistRepository : Repository<Artist> {
  fun getArtistByGenre(genreId: Long): Flow<PagingData<Artist>>

  fun getAlbumArtistsOnly(): Flow<PagingData<Artist>>
}
