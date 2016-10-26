package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.data.dao.ConnectionSettings
import com.kelsos.mbrc.dto.DiscoveryResponse

object DeviceSettingsMapper {
  fun fromResponse(response: DiscoveryResponse): ConnectionSettings {
    val settings = ConnectionSettings()
    settings.address = response.address!!
    settings.name = response.name!!
    settings.port = response.port
    return settings
  }
}
