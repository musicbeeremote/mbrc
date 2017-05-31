package com.kelsos.mbrc.content.now_playing.queue

import com.kelsos.mbrc.content.now_playing.queue.Queue.QueueType
import io.reactivex.Single

interface QueueApi {
  fun queue(@QueueType type: String, tracks: List<String>, play: String? = null): Single<QueueResponse>
}
