package com.kelsos.mbrc.content.library.albums

import androidx.paging.DataSource
import com.kelsos.mbrc.content.library.covers.AlbumCover
import com.kelsos.mbrc.interfaces.data.Repository

interface AlbumRepository : Repository<Album> {
  suspend fun getAlbumsByArtist(artist: String): DataSource.Factory<Int, Album>
  suspend fun updateCovers(updated: List<AlbumCover>)
  suspend fun getCovers(): List<AlbumCover>
}
