package com.kelsos.mbrc.networking.connections

import androidx.lifecycle.LiveData

interface ConnectionRepository {
  suspend fun save(settings: ConnectionSettingsEntity)

  suspend fun delete(settings: ConnectionSettingsEntity)

  var default: ConnectionSettingsEntity?

  fun getAll(): LiveData<List<ConnectionSettingsEntity>>

  fun count(): Long

  val defaultId: Long
}