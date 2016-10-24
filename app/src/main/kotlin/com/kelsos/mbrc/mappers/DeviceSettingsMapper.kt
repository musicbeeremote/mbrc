package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.data.dao.DeviceSettings
import com.kelsos.mbrc.dto.DiscoveryResponse

object DeviceSettingsMapper {
  fun fromResponse(response: DiscoveryResponse): DeviceSettings {
    val settings = DeviceSettings()
    settings.address = response.address
    settings.name = response.name
    settings.port = response.port
    return settings
  }
}
