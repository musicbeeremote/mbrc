package com.kelsos.mbrc.networking.connections

import androidx.lifecycle.LiveData
import com.kelsos.mbrc.ui.connectionmanager.ConnectionModel

interface ConnectionRepository {
  suspend fun save(settings: ConnectionSettingsEntity)

  suspend fun delete(settings: ConnectionSettingsEntity)

  suspend fun getAll(): LiveData<List<ConnectionSettingsEntity>>

  suspend fun count(): Long

  suspend fun setDefault(settings: ConnectionSettingsEntity)

  suspend fun getDefault(): ConnectionSettingsEntity?

  val defaultId: Long

  suspend fun getModel(): ConnectionModel
}
