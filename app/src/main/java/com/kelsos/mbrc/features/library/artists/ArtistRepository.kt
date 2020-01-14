package com.kelsos.mbrc.features.library.artists

import androidx.paging.PagingData
import com.kelsos.mbrc.interfaces.data.Repository
import kotlinx.coroutines.flow.Flow

interface ArtistRepository : Repository<Artist> {
  fun getArtistByGenre(genre: String): Flow<PagingData<Artist>>
  fun getAlbumArtistsOnly(): Flow<PagingData<Artist>>
}
