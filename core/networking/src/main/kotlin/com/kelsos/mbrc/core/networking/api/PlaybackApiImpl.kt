package com.kelsos.mbrc.core.networking.api

import com.kelsos.mbrc.core.common.data.Progress
import com.kelsos.mbrc.core.networking.ApiBase
import com.kelsos.mbrc.core.networking.dto.NowPlayingDto
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.payloads.CoverPayload
import com.kelsos.mbrc.core.networking.protocol.payloads.NowPlayingDetailsPayload
import kotlinx.coroutines.flow.Flow

class PlaybackApiImpl(private val apiBase: ApiBase) : PlaybackApi {
  override fun getNowPlayingList(progress: Progress?): Flow<List<NowPlayingDto>> =
    apiBase.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class, progress)

  override suspend fun getCover(): CoverPayload =
    apiBase.getItem(Protocol.NowPlayingCover, CoverPayload::class)

  override suspend fun getTrackDetails(): NowPlayingDetailsPayload =
    apiBase.getItem(Protocol.NowPlayingDetails, NowPlayingDetailsPayload::class)
}
