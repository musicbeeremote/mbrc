package com.kelsos.mbrc.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.Page
import com.kelsos.mbrc.data.Playlist
import com.kelsos.mbrc.extensions.toPage
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.utilities.SettingsManager
import io.reactivex.Observable
import javax.inject.Inject

class PlaylistServiceImpl
@Inject
constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    settingsManager: SettingsManager
) : ServiceBase(repository, mapper, settingsManager), PlaylistService {

  override fun getPlaylists(offset: Int, limit: Int): Observable<Page<Playlist>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.PlaylistList, range).flatMap { it.toPage<Playlist>(mapper) }
  }
}

