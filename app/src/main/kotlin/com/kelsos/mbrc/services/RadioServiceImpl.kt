package com.kelsos.mbrc.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.Page
import com.kelsos.mbrc.data.RadioStation
import com.kelsos.mbrc.extensions.toPage
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.utilities.SettingsManager
import io.reactivex.Observable
import javax.inject.Inject

class RadioServiceImpl
@Inject constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    settingsManager: SettingsManager
) : RadioService, ServiceBase(repository, mapper, settingsManager) {
  override fun getRadios(offset: Int, limit: Int): Observable<Page<RadioStation>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.RadioStations, range).flatMap { it.toPage<RadioStation>(mapper) }
  }
}
