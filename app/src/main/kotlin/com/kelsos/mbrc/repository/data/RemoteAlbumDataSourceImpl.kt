package com.kelsos.mbrc.repository.data

import com.kelsos.mbrc.dto.library.AlbumDto
import com.kelsos.mbrc.services.api.LibraryService
import rx.Observable
import javax.inject.Inject

class RemoteAlbumDataSourceImpl
@Inject constructor(private val service: LibraryService) : RemoteAlbumDataSource {
  /**
   * Retrieves all the available data from a remote data source
   */
  override fun fetch(): Observable<List<AlbumDto>> {
    throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}
