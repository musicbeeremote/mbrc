package com.kelsos.mbrc.features.queue

import com.kelsos.mbrc.common.Meta.Type
import com.kelsos.mbrc.features.queue.Queue.Action

interface QueueUseCase {
  suspend fun queue(
    id: Int,
    @Type meta: Int,
    @Action action: String = Queue.DEFAULT
  )
}
