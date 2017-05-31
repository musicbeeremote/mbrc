package com.kelsos.mbrc.content.library.albums

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.networking.ApiBase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteAlbumDataSource
@Inject constructor(private val service: ApiBase) : RemoteDataSource<Album> {
  override suspend fun fetch(): Flow<List<Album>> {
    return service.getAllPages(Protocol.LibraryBrowseAlbums, Album::class)
  }
}
