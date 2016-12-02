package com.kelsos.mbrc.ui.connection_manager

import com.kelsos.mbrc.data.dao.ConnectionSettings

data class ConnectionModel(val defaultId: Long, val settings: List<ConnectionSettings>)
