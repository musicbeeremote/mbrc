package com.kelsos.mbrc.content.library.albums

import androidx.paging.PagingData
import com.kelsos.mbrc.covers.AlbumCover
import com.kelsos.mbrc.interfaces.data.Repository
import kotlinx.coroutines.flow.Flow

interface AlbumRepository : Repository<Album> {
  fun getAlbumsByArtist(artist: String): Flow<PagingData<Album>>
  suspend fun updateCovers(updated: List<AlbumCover>)
  suspend fun getCovers(): List<AlbumCover>
}
