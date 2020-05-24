package com.kelsos.mbrc.features.queue

import arrow.core.Either
import com.kelsos.mbrc.common.Meta.Type
import com.kelsos.mbrc.features.queue.Queue.Action

interface QueueUseCase {
  suspend fun queue(
    id: Long,
    @Type meta: Int,
    @Action action: String = Queue.DEFAULT
  ): Either<Throwable, Int>
}