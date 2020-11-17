package com.kelsos.mbrc.networking.connections

import androidx.lifecycle.LiveData
import com.kelsos.mbrc.networking.discovery.DiscoveryStop

interface ConnectionRepository {
  suspend fun save(settings: ConnectionSettingsEntity)

  suspend fun delete(settings: ConnectionSettingsEntity)

  fun getAll(): LiveData<List<ConnectionSettingsEntity>>

  suspend fun count(): Long

  fun getDefault(): ConnectionSettingsEntity?

  fun setDefault(settings: ConnectionSettingsEntity)

  fun defaultSettings(): LiveData<ConnectionSettingsEntity?>

  suspend fun discover(): DiscoveryStop
}