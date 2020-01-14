package com.kelsos.mbrc.features.library.albums

import androidx.paging.DataSource
import com.kelsos.mbrc.features.library.DataModel
import com.kelsos.mbrc.interfaces.data.Repository

interface AlbumRepository : Repository<Album> {
  fun getAlbumsByArtist(artist: String): DataSource.Factory<Int, Album>

  /**
   * Retrieves the albums ordered by
   */
  fun getAlbumsSorted(): DataModel<Album>
}