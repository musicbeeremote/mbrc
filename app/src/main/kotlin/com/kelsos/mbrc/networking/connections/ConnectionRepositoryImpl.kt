package com.kelsos.mbrc.networking.connections

import androidx.lifecycle.LiveData
import com.kelsos.mbrc.content.activestatus.livedata.DefaultSettingsLiveDataProvider
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ConnectionRepositoryImpl(
  private val connectionDao: ConnectionDao,
  private val defaultSettingsLiveDataProvider: DefaultSettingsLiveDataProvider,
  private val dispatchers: AppCoroutineDispatchers,
  private val defaultSettingsModel: DefaultSettingsModel
) : ConnectionRepository {

  init {
    runBlocking {
      getDefault()?.let {
        defaultSettingsLiveDataProvider.update(it)
      }
    }
  }

  override suspend fun save(settings: ConnectionSettingsEntity) =
    withContext(dispatchers.database) {

      val id = connectionDao.findId(settings.address, settings.port)
      if (id != null) {
        settings.id = id
      }

      if (settings.id > 0) {
        connectionDao.update(settings)
      } else {
        connectionDao.insert(settings)
      }

      val newDefault = last
      if (count() == 1L && newDefault !== null) {
        setDefault(newDefault)
      }
    }

  override suspend fun delete(settings: ConnectionSettingsEntity) =
    withContext(dispatchers.database) {
      val oldId = settings.id

      connectionDao.delete(settings)

      if (oldId != defaultId) {
        return@withContext
      }

      val count = count()
      if (count == 0L) {
        defaultId = -1
      } else {
        val before = getItemBefore(oldId)
        val newDefault = before ?: first
        if (newDefault === null) {
          return@withContext
        }
        setDefault(newDefault)
      }
    }

  private fun getItemBefore(id: Long): ConnectionSettingsEntity? {
    return connectionDao.getPrevious(id)
  }

  private val first: ConnectionSettingsEntity?
    get() = connectionDao.first()

  private val last: ConnectionSettingsEntity?
    get() = connectionDao.last()

  override suspend fun setDefault(settings: ConnectionSettingsEntity) {
    defaultId = settings.id
    defaultSettingsLiveDataProvider.update(settings)
  }

  override suspend fun getDefault(): ConnectionSettingsEntity? = withContext(dispatchers.database) {
    val defaultId = defaultId
    if (defaultId < 0) {
      return@withContext null
    }
    return@withContext connectionDao.findById(defaultId)
  }

  override var defaultId: Long
    get() = defaultSettingsModel.defaultId
    private set(id) {
      defaultSettingsModel.defaultId = id
    }

  override suspend fun getAll(): LiveData<List<ConnectionSettingsEntity>> = connectionDao.getAll()

  override suspend fun count(): Long = withContext(dispatchers.database) {
    return@withContext connectionDao.count()
  }
}
