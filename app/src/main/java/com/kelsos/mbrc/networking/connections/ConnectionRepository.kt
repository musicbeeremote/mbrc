package com.kelsos.mbrc.networking.connections

import androidx.lifecycle.LiveData
import arrow.core.Option

interface ConnectionRepository {
  suspend fun save(settings: ConnectionSettingsEntity)

  suspend fun delete(settings: ConnectionSettingsEntity)

  fun getAll(): LiveData<List<ConnectionSettingsEntity>>

  suspend fun count(): Long

  fun getDefault(): Option<ConnectionSettingsEntity>

  fun setDefault(settings: ConnectionSettingsEntity)

  suspend fun defaultSettings(): LiveData<ConnectionSettingsEntity?>

  suspend fun discover(): Int
}
