package com.kelsos.mbrc.core.networking.api

import com.kelsos.mbrc.core.networking.ApiBase
import com.kelsos.mbrc.core.networking.dto.QueuePayload
import com.kelsos.mbrc.core.networking.dto.QueueResponse
import com.kelsos.mbrc.core.networking.protocol.base.Protocol

class QueueApiImpl(private val apiBase: ApiBase) : QueueApi {
  override suspend fun queue(payload: QueuePayload): QueueResponse =
    apiBase.getItem(Protocol.NowPlayingQueue, QueueResponse::class, payload)
}
