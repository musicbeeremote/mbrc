package com.kelsos.mbrc.content.playlists

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.Page
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.ClientInformationStore
import io.reactivex.Observable
import javax.inject.Inject

class PlaylistApiImpl
@Inject
constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    clientInformationStore: ClientInformationStore
) : ApiBase(repository, mapper, clientInformationStore), PlaylistService {

  override fun fetch(offset: Int, limit: Int): Observable<Page<PlaylistDto>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.PlaylistList, range).flatMap {
      return@flatMap Observable.fromCallable { mapper.readValue<Page<PlaylistDto>>(it.data as String) }
    }
  }
}

