package com.kelsos.mbrc.content.nowplaying.queue

import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.Action
import io.reactivex.Single

interface QueueApi {
  fun queue(@Action type: String, tracks: List<String>, play: String? = null): Single<QueueResponse>
}