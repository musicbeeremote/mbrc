package com.kelsos.mbrc.content.library.albums

import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteAlbumDataSource
@Inject
constructor(private val service: ApiBase) : RemoteDataSource<AlbumDto> {
  override suspend fun fetch(): Flow<List<AlbumDto>> {
    return service.getAllPages(Protocol.LibraryBrowseAlbums, AlbumDto::class)
  }
}
