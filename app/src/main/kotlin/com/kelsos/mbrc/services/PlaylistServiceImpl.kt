package com.kelsos.mbrc.services

import com.fasterxml.jackson.core.type.TypeReference
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.Page
import com.kelsos.mbrc.data.Playlist
import com.kelsos.mbrc.data.SocketMessage
import io.reactivex.Observable
import java.io.IOException
import javax.inject.Inject

class PlaylistServiceImpl
@Inject
constructor() : ServiceBase(), PlaylistService {

  override fun getPlaylists(offset: Int, limit: Int): Observable<Page<Playlist>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.PlaylistList, range ?: "").flatMap { this.getPageObservable(it) }
  }

  private fun getPageObservable(socketMessage: SocketMessage): Observable<Page<Playlist>> {
    return Observable.create<Page<Playlist>> { emitter ->
      try {
        val typeReference = object : TypeReference<Page<Playlist>>() {}
        val page = mapper.readValue<Page<Playlist>>(socketMessage.data as String, typeReference)
        emitter.onNext(page)
        emitter.onComplete()
      } catch (e: IOException) {
        emitter.onError(e)
      }
    }
  }
}

