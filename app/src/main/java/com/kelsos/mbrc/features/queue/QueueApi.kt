package com.kelsos.mbrc.features.queue

import arrow.core.Either

interface QueueApi {
  suspend fun queue(
    type: Queue,
    tracks: List<String>,
    play: String? = null
  ): Either<Throwable, QueueResponse>
}
