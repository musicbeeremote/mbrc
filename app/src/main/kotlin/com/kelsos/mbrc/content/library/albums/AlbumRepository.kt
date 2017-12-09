package com.kelsos.mbrc.content.library.albums

import com.kelsos.mbrc.content.library.covers.AlbumCover
import com.kelsos.mbrc.interfaces.data.Repository

interface AlbumRepository : Repository<Album> {
  suspend fun getAlbumsByArtist(artist: String): List<Album>
  suspend fun updateCovers(updated: List<AlbumCover>)
}
