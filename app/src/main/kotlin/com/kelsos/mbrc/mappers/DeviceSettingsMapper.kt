package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.domain.DeviceSettings
import com.kelsos.mbrc.dto.DiscoveryResponse

object DeviceSettingsMapper {
    fun fromResponse(response: DiscoveryResponse): DeviceSettings {
        val settings = DeviceSettings()
        settings.address = response.address
        settings.name = response.name
        settings.port = response.port
        settings.http = response.http
        return settings
    }
}
