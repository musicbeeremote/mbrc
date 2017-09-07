package com.kelsos.mbrc.content.library.albums

import com.kelsos.mbrc.interfaces.data.Repository
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Single

interface AlbumRepository : Repository<Album> {
  fun getAlbumsByArtist(artist: String): Single<FlowCursorList<Album>>

  /**
   * Retrieves the albums ordered by
   */
  fun getAlbumsSorted(
      @Sorting.Order order: Long = Sorting.ALBUM_ARTIST__ALBUM,
      ascending: Boolean = true
  ): Single<FlowCursorList<Album>>
}
