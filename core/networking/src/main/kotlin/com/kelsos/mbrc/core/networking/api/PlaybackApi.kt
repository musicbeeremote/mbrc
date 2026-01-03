package com.kelsos.mbrc.core.networking.api

import com.kelsos.mbrc.core.common.data.Progress
import com.kelsos.mbrc.core.networking.dto.NowPlayingDto
import com.kelsos.mbrc.core.networking.protocol.payloads.CoverPayload
import kotlinx.coroutines.flow.Flow

interface PlaybackApi {
  fun getNowPlayingList(progress: Progress?): Flow<List<NowPlayingDto>>

  suspend fun getCover(): CoverPayload
}
