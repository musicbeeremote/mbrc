package com.kelsos.mbrc.features.queue

import arrow.core.Either
import com.kelsos.mbrc.features.queue.Queue.Action

interface QueueApi {
  suspend fun queue(
    @Action type: String,
    tracks: List<String>,
    play: String? = null
  ): Either<Throwable, QueueResponse>
}