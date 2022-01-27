package com.kelsos.mbrc.features.work

import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.features.queue.Queue

interface WorkHandler {
  fun queue(
    id: Long,
    meta: Meta,
    action: Queue = Queue.Default,
  )
}
