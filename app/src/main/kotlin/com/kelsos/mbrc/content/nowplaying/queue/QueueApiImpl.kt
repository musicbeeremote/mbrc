package com.kelsos.mbrc.content.nowplaying.queue

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.content.nowplaying.cover.CoverPayload
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.Action
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.ClientInformationStore
import io.reactivex.Single
import javax.inject.Inject

class QueueApiImpl
@Inject constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    clientInformationStore: ClientInformationStore
) : ApiBase(repository, mapper, clientInformationStore), QueueApi {
  override fun queue(@Action type: String, tracks: List<String>, play: String?): Single<QueueResponse> {
    return request(Protocol.NowPlayingQueue, QueuePayload(type, tracks, play)).firstOrError().flatMap {
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
