package com.kelsos.mbrc.services

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.CoverPayload
import com.kelsos.mbrc.data.QueuePayload
import com.kelsos.mbrc.data.QueueResponse
import rx.Single
import javax.inject.Inject

class QueueServiceImpl
@Inject constructor() : ServiceBase(), QueueService {
  override fun queue(type: String, tracks: List<String>): Single<QueueResponse> {
    return request(Protocol.NowPlayingQueue, QueuePayload(type, tracks)).first().toSingle().flatMap {
      return@flatMap Single.fromCallable {
        val payload = mapper.readValue<QueueResponse>(it.data as String, QueueResponse::class.java)
        if (payload.code == CoverPayload.SUCCESS) {
          return@fromCallable payload
        } else {
          throw RuntimeException("Queueing of tracks failed")
        }
      }
    }
  }
}
