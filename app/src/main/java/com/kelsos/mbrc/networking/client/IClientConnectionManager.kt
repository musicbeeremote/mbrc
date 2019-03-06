package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity

interface IClientConnectionManager {
  fun setDefaultConnectionSettings(connectionSettings: ConnectionSettingsEntity)
  fun start()
  fun stop()
}