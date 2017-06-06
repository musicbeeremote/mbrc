package com.kelsos.mbrc.content.now_playing

import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteNowPlayingDataSource
@Inject constructor(private val service: ApiBase) : RemoteDataSource<NowPlaying> {
  override suspend fun fetch(): Flow<List<NowPlaying>> {
    return service.getAllPages(Protocol.NowPlayingList, NowPlaying::class)
  }
}
