package com.kelsos.mbrc.networking.connections

import androidx.lifecycle.LiveData
import com.kelsos.mbrc.content.activestatus.livedata.DefaultSettingsLiveDataProvider
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.experimental.launch

class ConnectionRepositoryImpl(
  private val connectionDao: ConnectionDao,
  private val defaultSettingsLiveDataProvider: DefaultSettingsLiveDataProvider,
  private val dispatchers: AppCoroutineDispatchers,
  private val defaultSettingsModel: DefaultSettingsModel
) : ConnectionRepository {

  init {
    launch(dispatchers.disk) {
      default?.let {
        defaultSettingsLiveDataProvider.update(it)
      }
    }
  }

  override fun save(settings: ConnectionSettingsEntity) {
    launch(dispatchers.database) {

      val id = connectionDao.findId(settings.address, settings.port)
      id?.let {
        settings.id = it
      }

      if (settings.id > 0) {
        connectionDao.update(settings)
      } else {
        connectionDao.insert(settings)
      }

      if (count() == 1L) {
        default = last
      }
    }
  }

  override fun delete(settings: ConnectionSettingsEntity) {
    launch(dispatchers.database) {
      val oldId = settings.id

      connectionDao.delete(settings)

      if (oldId == defaultId) {
        val count = count()
        if (count == 0L) {
          defaultId = -1
        } else {
          val before = getItemBefore(oldId)
          default = before ?: first
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

  override var default: ConnectionSettingsEntity?
    get() {
      val defaultId = defaultId
      if (defaultId < 0) {
        return null
      }

      return connectionDao.findById(defaultId)
    }
    set(settings) {
      if (settings == null) {
        return
      }
      defaultId = settings.id

      defaultSettingsLiveDataProvider.update(settings)
    }

  override var defaultId: Long
    get() = defaultSettingsModel.defaultId
    private set(id) {
      defaultSettingsModel.defaultId = id
    }

  override fun getAll(): LiveData<List<ConnectionSettingsEntity>> = connectionDao.getAll()

  override fun count(): Long {
    return connectionDao.count()
  }
}