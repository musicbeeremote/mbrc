package com.kelsos.mbrc.content.playlists

import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemotePlaylistDataSource
@Inject constructor(private val service: ApiBase) : RemoteDataSource<PlaylistDto> {
  override suspend fun fetch(): Flow<List<PlaylistDto>> {
    return service.getAllPages(Protocol.PlaylistList, PlaylistDto::class)
  }
}
