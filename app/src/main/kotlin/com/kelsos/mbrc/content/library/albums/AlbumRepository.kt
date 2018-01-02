package com.kelsos.mbrc.content.library.albums

import android.arch.lifecycle.LiveData
import com.kelsos.mbrc.interfaces.data.Repository
import io.reactivex.Single

interface AlbumRepository : Repository<AlbumEntity> {
  fun getAlbumsByArtist(artist: String): Single<LiveData<List<AlbumEntity>>>

  /**
   * Retrieves the albums ordered by
   */
  fun getAlbumsSorted(
      @Sorting.Fields order: Long = Sorting.ALBUM_ARTIST__ALBUM,
      ascending: Boolean = true
  ): Single<LiveData<List<AlbumEntity>>>
}
