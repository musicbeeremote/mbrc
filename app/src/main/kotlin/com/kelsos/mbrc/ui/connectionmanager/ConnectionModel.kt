package com.kelsos.mbrc.ui.connectionmanager

import androidx.lifecycle.LiveData
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity

data class ConnectionModel(
  val defaultId: Long,
  val settings: LiveData<List<ConnectionSettingsEntity>>
)
