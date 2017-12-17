package com.kelsos.mbrc.content.radios

import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteRadioDataSource
@Inject constructor(private val service: ApiBase) : RemoteDataSource<RadioStationDto> {
  override suspend fun fetch(): Flow<List<RadioStationDto>> {
    return service.getAllPages(Protocol.RadioStations, RadioStationDto::class)
  }
}
