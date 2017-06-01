package com.kelsos.mbrc.content.playlists

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.Page
import com.kelsos.mbrc.preferences.SettingsManager
import io.reactivex.Observable
import javax.inject.Inject

class PlaylistApiImpl
@Inject
constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    settingsManager: SettingsManager
) : ApiBase(repository, mapper, settingsManager), PlaylistService {

  override fun getPlaylists(offset: Int, limit: Int): Observable<Page<Playlist>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.PlaylistList, range).flatMap {
      Observable.fromCallable { mapper.readValue<Page<Playlist>>(it.data as String) }
    }
  }
}

