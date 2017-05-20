package com.kelsos.mbrc.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.NowPlaying
import com.kelsos.mbrc.data.Page
import com.kelsos.mbrc.extensions.toPage
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.utilities.SettingsManager
import io.reactivex.Observable
import javax.inject.Inject

class NowPlayingServiceImpl
@Inject
constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    settingsManager: SettingsManager
) : ServiceBase(repository, mapper, settingsManager), NowPlayingService {

  override fun getNowPlaying(offset: Int, limit: Int): Observable<Page<NowPlaying>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.NowPlayingList, range).flatMap { it.toPage<NowPlaying>(mapper) }
  }
}
