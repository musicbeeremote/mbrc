package com.kelsos.mbrc.content.library.tracks

import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteTrackDataSource
@Inject
constructor(private val service: ApiBase) : RemoteDataSource<TrackDto> {
  override suspend fun fetch(): Flow<List<TrackDto>> {
    return service.getAllPages(Protocol.LibraryBrowseTracks, TrackDto::class)
  }
}
