package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity

interface IClientConnectionManager {
  fun setDefaultConnectionSettings(connectionSettings: ConnectionSettingsEntity)
  fun setOnConnectionChangeListener(onConnectionChange: (Boolean) -> Unit)
  fun start()
  fun stop()
}
