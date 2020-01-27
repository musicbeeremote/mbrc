package com.kelsos.mbrc.features.queue

import com.kelsos.mbrc.features.player.cover.CoverPayload
import com.kelsos.mbrc.features.queue.Queue.Action
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import io.reactivex.Single

class QueueApiImpl(
  private val apiBase: ApiBase
) : QueueApi {
  override fun queue(
    @Action type: String,
    tracks: List<String>,
    play: String?
  ): Single<QueueResponse> {
    return apiBase.getItem(
      Protocol.NowPlayingQueue,
      QueueResponse::class,
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