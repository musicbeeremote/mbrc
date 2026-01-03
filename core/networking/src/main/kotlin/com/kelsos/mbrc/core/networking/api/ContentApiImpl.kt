package com.kelsos.mbrc.core.networking.api

import com.kelsos.mbrc.core.common.data.Progress
import com.kelsos.mbrc.core.networking.ApiBase
import com.kelsos.mbrc.core.networking.dto.PlaylistDto
import com.kelsos.mbrc.core.networking.dto.RadioStationDto
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import kotlinx.coroutines.flow.Flow

class ContentApiImpl(private val apiBase: ApiBase) : ContentApi {
  override fun getPlaylists(progress: Progress?): Flow<List<PlaylistDto>> =
    apiBase.getAllPages(Protocol.PlaylistList, PlaylistDto::class, progress)

  override fun getRadioStations(progress: Progress?): Flow<List<RadioStationDto>> =
    apiBase.getAllPages(Protocol.RadioStations, RadioStationDto::class, progress)
}
