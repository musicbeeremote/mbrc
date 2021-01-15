package com.kelsos.mbrc.features.queue

import arrow.core.Either
import com.kelsos.mbrc.common.Meta

interface QueueUseCase {
  suspend fun queue(
    id: Long,
    meta: Meta,
    action: Queue = Queue.Default
  ): Either<Throwable, Int>
}
