package com.kelsos.mbrc.core.networking.api

import com.kelsos.mbrc.core.networking.dto.QueuePayload
import com.kelsos.mbrc.core.networking.dto.QueueResponse

interface QueueApi {
  suspend fun queue(payload: QueuePayload): QueueResponse
}
