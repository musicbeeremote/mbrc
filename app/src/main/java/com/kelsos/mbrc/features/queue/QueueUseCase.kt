package com.kelsos.mbrc.features.queue

import com.kelsos.mbrc.common.Meta

interface QueueUseCase {
  suspend fun queue(
    id: Long,
    meta: Meta,
    action: Queue = Queue.Default
  ): QueueResult

  suspend fun queuePath(path: String): QueueResult
}
