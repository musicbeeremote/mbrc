package com.kelsos.mbrc.content.nowplaying.queue

import com.kelsos.mbrc.content.nowplaying.queue.Queue.QueueType
import io.reactivex.Single

interface QueueApi {
  fun queue(@QueueType type: String, tracks: List<String>, play: String? = null): Single<QueueResponse>
}
