package com.kelsos.mbrc.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.annotations.Queue.QueueType
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.CoverPayload
import com.kelsos.mbrc.data.QueuePayload
import com.kelsos.mbrc.data.QueueResponse
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.utilities.SettingsManager
import io.reactivex.Single
import javax.inject.Inject

class QueueServiceImpl
@Inject constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    settingsManager: SettingsManager
) : ServiceBase(repository, mapper, settingsManager), QueueService {
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
