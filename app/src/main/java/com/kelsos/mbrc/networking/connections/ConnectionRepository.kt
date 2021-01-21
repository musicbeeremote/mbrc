package com.kelsos.mbrc.networking.connections

import androidx.lifecycle.LiveData
import com.kelsos.mbrc.networking.discovery.DiscoveryStop
import kotlinx.coroutines.flow.Flow

interface ConnectionRepository {
  suspend fun save(settings: ConnectionSettingsEntity)

  suspend fun delete(settings: ConnectionSettingsEntity)

  fun getAll(): LiveData<List<ConnectionSettingsEntity>>

  suspend fun count(): Long

  suspend fun getDefault(): ConnectionSettingsEntity?

  suspend fun setDefaultConnectionId(id: Long)

  fun getDefaultConnectionId(): Flow<Long>

  suspend fun discover(): DiscoveryStop
}
