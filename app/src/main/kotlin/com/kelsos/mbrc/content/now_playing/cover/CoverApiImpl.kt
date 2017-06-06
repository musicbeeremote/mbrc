package com.kelsos.mbrc.content.now_playing.cover

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.ClientInformationStore
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class CoverApiImpl
@Inject constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    clientInformationStore: ClientInformationStore
) : ApiBase(repository, mapper, clientInformationStore), CoverApi {
  override fun getCover(): Single<String> {
    return request(Protocol.NowPlayingCover).flatMap {
      return@flatMap Observable.fromCallable {
        val payload = mapper.readValue<CoverPayload>(it.data as String, CoverPayload::class.java)
        if (payload.status == CoverPayload.SUCCESS) {
          return@fromCallable payload.cover
        } else {
          throw RuntimeException("Cover not available")
        }
      }
    }.firstOrError()
  }
}
