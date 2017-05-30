package com.kelsos.mbrc.library.albums

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.repository.data.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteAlbumDataSource
@Inject constructor(private val service: ApiBase) : RemoteDataSource<Album> {
  override suspend fun fetch(): Flow<List<Album>> {
    return service.getAllPages(Protocol.LibraryBrowseAlbums, Album::class)
  }
}
