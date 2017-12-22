package com.kelsos.mbrc.ui.connectionmanager

import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity

data class ConnectionModel(
    val defaultId: Long,
    val settings: List<ConnectionSettingsEntity>
)
