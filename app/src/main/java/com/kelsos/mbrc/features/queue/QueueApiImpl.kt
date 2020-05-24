package com.kelsos.mbrc.features.queue

import arrow.core.Either
import com.kelsos.mbrc.features.player.cover.CoverPayload
import com.kelsos.mbrc.features.queue.Queue.Action
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol

class QueueApiImpl(
  private val apiBase: ApiBase
) : QueueApi {
  override suspend fun queue(
    @Action type: String,
    tracks: List<String>,
    play: String?
  ): Either<Throwable, QueueResponse> = Either.catch {
    val response = apiBase.getItem(
      Protocol.NowPlayingQueue,
      QueueResponse::class,
      QueuePayload(type, tracks, play)
    )

    if (response.code == CoverPayload.SUCCESS) {
      response
    } else {
      throw RuntimeException("Queueing of tracks failed")
    }
  }
}