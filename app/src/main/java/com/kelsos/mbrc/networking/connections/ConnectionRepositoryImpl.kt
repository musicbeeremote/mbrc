package com.kelsos.mbrc.networking.connections

import androidx.paging.PagingData
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.networking.discovery.DiscoveryStop
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscovery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber

class ConnectionRepositoryImpl(
  private val dao: ConnectionDao,
  private val dispatchers: AppCoroutineDispatchers,
  private val discovery: RemoteServiceDiscovery
) : ConnectionRepository {

  override suspend fun discover(): DiscoveryStop {
    val discover = discovery.discover()
    return discover.fold(
      { it },
      {
        Timber.v("Connection was successfully discovered $it")
        save(it.toConnection())
        DiscoveryStop.Complete
      }
    )
  }

  override suspend fun save(settings: ConnectionSettings) {
    withContext(dispatchers.database) {
      val entity = settings.toConnectionEntity()

      if (settings.id > 0) {
        dao.update(entity)
      } else {
        dao.insert(entity)
      }

      if (count() == 1L) {
        dao.updateDefault(checkNotNull(dao.last()?.toConnection()).id)
      }
    }
  }

  override suspend fun delete(settings: ConnectionSettings) {
    withContext(dispatchers.database) {
      val oldId = settings.id
      dao.delete(settings.toConnectionEntity())
      val default = dao.getDefault()
      if (default == null && count() > 0) {
        dao.updateDefault(checkNotNull(getItemBefore(oldId) ?: dao.first()?.toConnection()).id)
      }
    }
  }

  private fun getItemBefore(id: Long): ConnectionSettings? {
    return dao.getPrevious(id)?.toConnection()
  }

  override fun getDefault(): ConnectionSettings? {
    return dao.getDefault()?.toConnection()
  }

  override fun setDefault(settings: ConnectionSettings) {
    dao.updateDefault(settings.id)
  }

  override fun getAll(): Flow<PagingData<ConnectionSettings>> = paged({ dao.getAll() }) {
    it.toConnection()
  }

  override fun all(): List<ConnectionSettings> = dao.all().map { it.toConnection() }

  override suspend fun count(): Long {
    return withContext(dispatchers.database) {
      dao.count()
    }
  }
}

fun ConnectionSettingsEntity.toConnection(): ConnectionSettings {
  return ConnectionSettings(
    address = address,
    port = port,
    name = name,
    isDefault = isDefault ?: false,
    id = id
  )
}

fun ConnectionSettings.toConnectionEntity(): ConnectionSettingsEntity {
  val isDefault = if (isDefault) isDefault else null
  return ConnectionSettingsEntity(
    address = address,
    port = port,
    name = name,
    isDefault = isDefault,
    id = id
  )
}
