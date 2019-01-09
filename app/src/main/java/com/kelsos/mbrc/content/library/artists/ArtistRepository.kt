package com.kelsos.mbrc.content.library.artists

import androidx.paging.PagingData
import com.kelsos.mbrc.interfaces.data.Repository
import kotlinx.coroutines.flow.Flow

interface ArtistRepository : Repository<Artist> {
  suspend fun getArtistByGenre(genre: String): Flow<PagingData<Artist>>
  suspend fun getAlbumArtistsOnly(): Flow<PagingData<Artist>>
}
