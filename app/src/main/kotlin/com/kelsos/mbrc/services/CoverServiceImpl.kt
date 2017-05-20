package com.kelsos.mbrc.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.CoverPayload
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.utilities.SettingsManager
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class CoverServiceImpl
@Inject constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    settingsManager: SettingsManager
) : ServiceBase(repository, mapper, settingsManager), CoverService {
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
