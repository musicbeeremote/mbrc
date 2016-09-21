package com.kelsos.mbrc.services

import com.fasterxml.jackson.core.type.TypeReference
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.NowPlaying
import com.kelsos.mbrc.data.Page
import com.kelsos.mbrc.data.SocketMessage
import rx.Observable
import java.io.IOException
import javax.inject.Inject

class NowPlayingServiceImpl
@Inject
constructor() : ServiceBase(), NowPlayingService {

  override fun getNowPlaying(offset: Int, limit: Int): Observable<Page<NowPlaying>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.NowPlayingList, range ?: "").flatMap { this.getPageObservable(it) }
  }

  private fun getPageObservable(socketMessage: SocketMessage): Observable<Page<NowPlaying>> {
    return Observable.create<Page<NowPlaying>> { subscriber ->
      try {
        val typeReference = object : TypeReference<Page<NowPlaying>>() {}
        val page = mapper.readValue<Page<NowPlaying>>(socketMessage.data as String, typeReference)
        subscriber.onNext(page)
        subscriber.onCompleted()
      } catch (e: IOException) {
        subscriber.onError(e)
      }
    }
  }
}
