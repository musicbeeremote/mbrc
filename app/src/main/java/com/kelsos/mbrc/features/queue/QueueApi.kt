package com.kelsos.mbrc.features.queue

import com.kelsos.mbrc.features.queue.LibraryPopup.Action
import io.reactivex.Single

interface QueueApi {
  fun queue(@Action type: String, tracks: List<String>, play: String? = null): Single<QueueResponse>
}