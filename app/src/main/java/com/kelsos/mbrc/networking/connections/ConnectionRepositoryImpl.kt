package com.kelsos.mbrc.networking.connections

import androidx.paging.PagingData
import arrow.core.Option
import com.kelsos.mbrc.networking.discovery.DiscoveryStop
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscovery
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ConnectionRepositoryImpl(
  private val dao: ConnectionDao,
  private val dispatchers: AppCoroutineDispatchers,
  private val settings: DefaultSettingsModel,
  private val discovery: RemoteServiceDiscovery
) : ConnectionRepository {

  override suspend fun discover(): Int {
    val discover = discovery.discover()
    return discover.fold(
      { it },
      {
        save(it.toConnection())
        DiscoveryStop.COMPLETE
      }
    )
  }

  override suspend fun save(settings: ConnectionSettings) {
    withContext(dispatchers.database) {
      val entity = settings.toConnectionEntity()
      val id = dao.findId(settings.address, settings.port)
      id?.let {
        settings.id = it
      }

      if (settings.id > 0) {
        dao.update(entity)
      } else {
        settings.id = dao.insert(entity)
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

  override fun getDefault(): Option<ConnectionSettings> {
    return Option.fromNullable(dao.getDefault()?.toConnection())
  }

  override fun setDefault(settings: ConnectionSettings) {
    dao.updateDefault(settings.id)
  }

  override fun getAll(): Flow<PagingData<ConnectionSettings>> = dao.getAll().paged {
    it.toConnection()
  }

  override suspend fun count(): Long {
    return withContext(dispatchers.database) {
      dao.count()
    }
  }
}

private fun ConnectionSettingsEntity.toConnection(): ConnectionSettings {
  return ConnectionSettings(
    address = address,
    port = port,
    name = name,
    isDefault = isDefault ?: false,
    id = id
  )
}

private fun ConnectionSettings.toConnectionEntity(): ConnectionSettingsEntity {
  val isDefault = if (isDefault) isDefault else null
  return ConnectionSettingsEntity(
    address = address,
    port = port,
    name = name,
    isDefault = isDefault,
    id = id
  )
}
