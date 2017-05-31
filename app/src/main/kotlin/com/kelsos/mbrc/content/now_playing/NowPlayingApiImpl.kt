package com.kelsos.mbrc.content.now_playing

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.extensions.toPage
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.Page
import com.kelsos.mbrc.preferences.SettingsManager
import io.reactivex.Observable
import javax.inject.Inject

class NowPlayingApiImpl
@Inject
constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    settingsManager: SettingsManager
) : ApiBase(repository, mapper, settingsManager), NowPlayingService {

  override fun getNowPlaying(offset: Int, limit: Int): Observable<Page<NowPlaying>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.NowPlayingList, range).flatMap { it.toPage<NowPlaying>(mapper) }
  }
}
