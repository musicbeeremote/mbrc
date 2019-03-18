package com.kelsos.mbrc.networking.connections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import arrow.core.Option
import com.kelsos.mbrc.networking.discovery.DiscoveryStop
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscovery
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.withContext

class ConnectionRepositoryImpl(
  private val connectionDao: ConnectionDao,
  private val dispatchers: AppCoroutineDispatchers,
  private val defaultSettingsModel: DefaultSettingsModel,
  private val remoteServiceDiscovery: RemoteServiceDiscovery
) : ConnectionRepository {

  private val default: MediatorLiveData<ConnectionSettingsEntity> = MediatorLiveData()
  private var defaultData: LiveData<ConnectionSettingsEntity>? = null

  override suspend fun discover(): Int {
    val discover = remoteServiceDiscovery.discover()
    return discover.fold({ it }, {
      save(it)
      DiscoveryStop.COMPLETE
    })
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
        defaultSettingsModel.defaultId = checkNotNull(last).id
      }
    }
  }

  override suspend fun delete(settings: ConnectionSettingsEntity) {
    withContext(dispatchers.database) {
      val oldId = settings.id

      connectionDao.delete(settings)

      if (oldId == defaultId) {
        val count = count()
        if (count == 0L) {
          defaultId = -1
        } else {
          defaultSettingsModel.defaultId = checkNotNull(getItemBefore(oldId) ?: first).id
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

  override fun getDefault(): Option<ConnectionSettingsEntity> {
    val defaultId = defaultId
    if (defaultId < 0) {
      return Option.empty()
    }

    return Option.fromNullable(connectionDao.findById(defaultId))
  }

  override fun setDefault(settings: ConnectionSettingsEntity) {
    defaultId = settings.id
  }

  override fun defaultSettings(): LiveData<ConnectionSettingsEntity?> {
    if (defaultData == null) {
      observeDefault()
    }
    return default
  }

  private fun observeDefault() {
    val data = connectionDao.getById(defaultId).also {
      this.defaultData = it
    }
    default.addSource(data) { value ->
      default.value = value
    }
  }

  private var defaultId: Long
    get() = defaultSettingsModel.defaultId
    set(id) {
      defaultSettingsModel.defaultId = id
      defaultData?.let {
        default.removeSource(it)
      }
      observeDefault()
    }

  override fun getAll(): LiveData<List<ConnectionSettingsEntity>> = connectionDao.getAll()

  override suspend fun count(): Long {
    return withContext(dispatchers.database) {
      connectionDao.count()
    }
  }
}