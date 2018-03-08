package com.kelsos.mbrc.content.nowplaying

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.Page
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.ClientInformationStore
import io.reactivex.Observable
import javax.inject.Inject

class NowPlayingApiImpl
@Inject
constructor(
  repository: ConnectionRepository,
  private val mapper: ObjectMapper,
  clientInformationStore: ClientInformationStore
) : ApiBase(repository, mapper, clientInformationStore), NowPlayingService {

  override fun getNowPlaying(offset: Int, limit: Int): Observable<Page<NowPlayingDto>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.NowPlayingList, range).flatMap {
      return@flatMap Observable.fromCallable { mapper.readValue<Page<NowPlayingDto>>(it.data as String) }
    }
  }
}