package com.kelsos.mbrc.content.nowplaying

import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteNowPlayingDataSource
@Inject
constructor(private val service: ApiBase) : RemoteDataSource<NowPlayingDto> {
  override suspend fun fetch(): Flow<List<NowPlayingDto>> {
    return service.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class)
  }
}
