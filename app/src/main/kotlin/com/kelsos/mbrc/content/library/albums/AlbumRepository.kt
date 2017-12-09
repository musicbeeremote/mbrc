package com.kelsos.mbrc.content.library.albums

import com.kelsos.mbrc.interfaces.data.Repository
import io.reactivex.Single

interface AlbumRepository : Repository<Album> {
  fun getAlbumsByArtist(artist: String): Single<List<Album>>

  /**
   * Retrieves the albums ordered by
   */
  fun getAlbumsSorted(
      @Sorting.Fields order: Long = Sorting.ALBUM_ARTIST__ALBUM,
      ascending: Boolean = true
  ): Single<List<Album>>
}
