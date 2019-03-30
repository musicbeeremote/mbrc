package com.kelsos.mbrc.networking.connections

import androidx.paging.PagingData
import arrow.core.Option
import com.kelsos.mbrc.networking.discovery.DiscoveryStop
import kotlinx.coroutines.flow.Flow

interface ConnectionRepository {
  suspend fun save(settings: ConnectionSettings)

  suspend fun delete(settings: ConnectionSettings)

  fun getAll(): Flow<PagingData<ConnectionSettings>>

  suspend fun count(): Long

  fun getDefault(): Option<ConnectionSettings>

  fun setDefault(settings: ConnectionSettings)

  suspend fun discover(): DiscoveryStop
}
