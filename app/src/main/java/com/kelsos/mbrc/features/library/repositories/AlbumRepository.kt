package com.kelsos.mbrc.features.library.repositories

import androidx.paging.DataSource
import com.kelsos.mbrc.common.data.Repository
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.data.DataModel

interface AlbumRepository : Repository<Album> {
  fun getAlbumsByArtist(artist: String): DataSource.Factory<Int, Album>

  /**
   * Retrieves the albums ordered by
   */
  fun getAlbumsSorted(): DataModel<Album>
}