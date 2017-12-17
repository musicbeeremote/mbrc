package com.kelsos.mbrc.networking.connections

import com.kelsos.mbrc.ui.connectionmanager.ConnectionModel
import io.reactivex.Single

interface ConnectionRepository {
  fun save(settings: ConnectionSettingsEntity)

  fun delete(settings: ConnectionSettingsEntity)

  var default: ConnectionSettingsEntity?

  fun getAll(): List<ConnectionSettingsEntity>

  fun count(): Long

  val defaultId: Long

  fun getModel(): Single<ConnectionModel>
}
