package com.kelsos.mbrc.content.nowplaying.queue

import com.kelsos.mbrc.content.nowplaying.cover.CoverPayload
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.Action
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import io.reactivex.Single
import javax.inject.Inject

class QueueApiImpl
@Inject
constructor(
  private val apiBase: ApiBase
) : QueueApi {
  override fun queue(
    @Action type: String,
    tracks: List<String>,
    play: String?
  ): Single<QueueResponse> {
    return apiBase.getItem(
      Protocol.NowPlayingQueue,
      QueueResponse::class.java,
      QueuePayload(type, tracks, play)
    ).map { payload ->
      if (payload.code == CoverPayload.SUCCESS) {
        return@map payload
      } else {
        throw RuntimeException("Queueing of tracks failed")
      }
    }
  }
}