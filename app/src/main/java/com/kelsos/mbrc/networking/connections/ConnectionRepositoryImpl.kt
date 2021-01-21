package com.kelsos.mbrc.networking.connections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.kelsos.mbrc.common.utilities.AppDispatchers
import com.kelsos.mbrc.networking.discovery.DiscoveryStop
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscovery
import com.kelsos.mbrc.preferences.AppDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ConnectionRepositoryImpl(
  private val connectionDao: ConnectionDao,
  private val dispatchers: AppDispatchers,
  private val remoteServiceDiscovery: RemoteServiceDiscovery,
  private val appDataStore: AppDataStore
) : ConnectionRepository {

  private val default: MediatorLiveData<ConnectionSettingsEntity> = MediatorLiveData()
  private var defaultData: LiveData<ConnectionSettingsEntity>? = null

  override suspend fun discover(): DiscoveryStop {
    val discover = remoteServiceDiscovery.discover()
    return discover.fold(
      { it },
      {
        save(it)
        DiscoveryStop.Complete
      }
    )
  }

  override suspend fun save(settings: ConnectionSettingsEntity) {
    withContext(dispatchers.database) {
      val id = connectionDao.findId(settings.address, settings.port)
      id?.let {
        settings.id = it
      }

      if (settings.id > 0) {
        connectionDao.update(settings)
      } else {
        settings.id = connectionDao.insert(settings)
      }

      if (count() == 1L) {
        appDataStore.setDefaultConnectionId(checkNotNull(last).id)
      }
    }
  }

  override suspend fun delete(settings: ConnectionSettingsEntity) {
    withContext(dispatchers.database) {
      val oldId = settings.id
      connectionDao.delete(settings)
      val defaultConnectionId = appDataStore.getDefaultConnectionId().first()

      if (oldId == defaultConnectionId) {
        val count = count()
        if (count == 0L) {
          appDataStore.setDefaultConnectionId(-1)
        } else {
          appDataStore.setDefaultConnectionId(checkNotNull(getItemBefore(oldId) ?: first).id)
        }
      }
    }
  }

  private fun getItemBefore(id: Long): ConnectionSettingsEntity? {
    return connectionDao.getPrevious(id)
  }

  private val first: ConnectionSettingsEntity?
    get() = connectionDao.first()

  private val last: ConnectionSettingsEntity?
    get() = connectionDao.last()

  override suspend fun getDefault(): ConnectionSettingsEntity? {
    val defaultId = appDataStore.getDefaultConnectionId().first()
    if (defaultId < 0) {
      return null
    }

    return connectionDao.findById(defaultId)
  }

  override suspend fun setDefaultConnectionId(id: Long) {
    appDataStore.setDefaultConnectionId(id)
    observeDefault()
  }

  private suspend fun observeDefault() {
    defaultData?.let {
      default.removeSource(it)
    }
    val defaultConnectionId = appDataStore.getDefaultConnectionId().first()
    val data = connectionDao.getById(defaultConnectionId).also {
      this.defaultData = it
    }
    default.addSource(data) { value ->
      default.value = value
    }
  }

  override fun getDefaultConnectionId(): Flow<Long> {
    return appDataStore.getDefaultConnectionId()
  }

  override fun getAll(): LiveData<List<ConnectionSettingsEntity>> = connectionDao.getAll()

  override suspend fun count(): Long {
    return withContext(dispatchers.database) {
      connectionDao.count()
    }
  }
}
