package com.kelsos.mbrc.networking.discovery

import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity

interface RemoteServiceDiscovery {
  fun discover(
    callback: (
      status: Int,
      setting: ConnectionSettingsEntity?
    ) -> Unit
  )
}