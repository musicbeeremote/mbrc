package com.kelsos.mbrc.networking.connections

import androidx.annotation.VisibleForTesting
import androidx.paging.PagingData
import com.kelsos.mbrc.networking.discovery.DiscoveryStop
import kotlinx.coroutines.flow.Flow

interface ConnectionRepository {
  suspend fun save(settings: ConnectionSettings)

  suspend fun delete(settings: ConnectionSettings)

  fun getAll(): Flow<PagingData<ConnectionSettings>>

  @VisibleForTesting
  fun all(): List<ConnectionSettings>

  suspend fun count(): Long

  fun getDefault(): ConnectionSettings?

  fun setDefault(settings: ConnectionSettings)

  suspend fun discover(): DiscoveryStop
}
