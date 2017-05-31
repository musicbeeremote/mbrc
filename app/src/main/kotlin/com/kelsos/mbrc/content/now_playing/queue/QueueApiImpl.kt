package com.kelsos.mbrc.content.now_playing.queue

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.content.now_playing.cover.CoverPayload
import com.kelsos.mbrc.content.now_playing.queue.Queue.QueueType
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.preferences.SettingsManager
import io.reactivex.Single
import javax.inject.Inject

class QueueApiImpl
@Inject constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    settingsManager: SettingsManager
) : ApiBase(repository, mapper, settingsManager), QueueApi {
  override fun queue(@QueueType type: String, tracks: List<String>, play: String?): Single<QueueResponse> {
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
