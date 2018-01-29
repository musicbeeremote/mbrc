package com.kelsos.mbrc.content.library.albums

import android.arch.paging.DataSource
import com.kelsos.mbrc.interfaces.data.Repository
import io.reactivex.Single

interface AlbumRepository : Repository<AlbumEntity> {
  fun getAlbumsByArtist(artist: String): Single<DataSource.Factory<Int, AlbumEntity>>

  /**
   * Retrieves the albums ordered by
   */
  fun getAlbumsSorted(
      @Sorting.Fields order: Long = Sorting.ALBUM_ARTIST__ALBUM,
      ascending: Boolean = true
  ): Single<DataSource.Factory<Int, AlbumEntity>>
}
