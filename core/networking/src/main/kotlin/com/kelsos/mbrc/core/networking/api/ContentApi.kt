package com.kelsos.mbrc.core.networking.api

import com.kelsos.mbrc.core.common.data.Progress
import com.kelsos.mbrc.core.networking.dto.PlaylistDto
import com.kelsos.mbrc.core.networking.dto.RadioStationDto
import kotlinx.coroutines.flow.Flow

interface ContentApi {
  fun getPlaylists(progress: Progress?): Flow<List<PlaylistDto>>

  fun getRadioStations(progress: Progress?): Flow<List<RadioStationDto>>
}
