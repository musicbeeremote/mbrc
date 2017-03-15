package com.kelsos.mbrc.services

import com.fasterxml.jackson.core.type.TypeReference
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.Page
import com.kelsos.mbrc.data.RadioStation
import com.kelsos.mbrc.data.SocketMessage
import io.reactivex.Observable
import java.io.IOException
import javax.inject.Inject

class RadioServiceImpl
@Inject constructor() : RadioService, ServiceBase() {
  override fun getRadios(offset: Int, limit: Int): Observable<Page<RadioStation>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.RadioStations, range ?: "").flatMap { getPageObservable(it) }
  }

  private fun getPageObservable(socketMessage: SocketMessage): Observable<Page<RadioStation>> {
    return Observable.create<Page<RadioStation>> { emitter ->
      try {
        val typeReference = object : TypeReference<Page<RadioStation>>() {}
        val page = mapper.readValue<Page<RadioStation>>(socketMessage.data as String, typeReference)
        emitter.onNext(page)
        emitter.onComplete()
      } catch (e: IOException) {
        emitter.onError(e)
      }
    }
  }
}
