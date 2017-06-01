package com.kelsos.mbrc.content.radios

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kelsos.mbrc.constants.Protocol
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
    return request(Protocol.RadioStations, range).flatMap {
      Observable.fromCallable { mapper.readValue<Page<RadioStation>>(it.data as String) }
    }
  }
}
