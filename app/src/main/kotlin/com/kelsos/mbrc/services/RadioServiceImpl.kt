package com.kelsos.mbrc.services

import com.fasterxml.jackson.core.type.TypeReference
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.RadioStation
import rx.Single
import javax.inject.Inject

class RadioServiceImpl
@Inject constructor() : RadioService, ServiceBase() {
  override fun getRadios(): Single<List<RadioStation>> {
    return request(Protocol.RadioStations).first().toSingle().flatMap {
      return@flatMap Single.fromCallable {
        val typeReference = object : TypeReference<List<RadioStation>>() {}
        val page = mapper.readValue<List<RadioStation>>(it.data as String, typeReference)
        return@fromCallable page
      }
    }
  }
}
