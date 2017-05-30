package com.kelsos.mbrc.library.tracks

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.repository.data.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteTrackDataSource
@Inject constructor(private val service: ApiBase) : RemoteDataSource<Track> {
  override suspend fun fetch(): Flow<List<Track>> {
    return service.getAllPages(Protocol.LibraryBrowseTracks, Track::class)
  }
}
