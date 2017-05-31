package com.kelsos.mbrc.content.radios

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.extensions.toPage
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.Page
import com.kelsos.mbrc.preferences.SettingsManager
import io.reactivex.Observable
import javax.inject.Inject

class RadioApiImpl
@Inject constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    settingsManager: SettingsManager
) : RadioApi, ApiBase(repository, mapper, settingsManager) {
  override fun getRadios(offset: Int, limit: Int): Observable<Page<RadioStation>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.RadioStations, range).flatMap { it.toPage<RadioStation>(mapper) }
  }
}
