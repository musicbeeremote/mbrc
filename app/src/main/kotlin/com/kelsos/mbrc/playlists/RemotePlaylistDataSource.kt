package com.kelsos.mbrc.playlists

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.repository.data.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemotePlaylistDataSource
@Inject constructor(private val service: ApiBase) : RemoteDataSource<Playlist> {
  override suspend fun fetch(): Flow<List<Playlist>> {
    return service.getAllPages(Protocol.PlaylistList, Playlist::class)
  }
}
