package com.kelsos.mbrc.ui.connection_manager

import com.kelsos.mbrc.networking.connections.ConnectionSettings

data class ConnectionModel(val defaultId: Long, val settings: List<ConnectionSettings>)
