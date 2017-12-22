package com.kelsos.mbrc.networking.connections

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.lifecycle.LiveData
import com.kelsos.mbrc.R
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.ui.connectionmanager.ConnectionModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ConnectionRepositoryImpl
@Inject
constructor(
  private val connectionDao: ConnectionDao,
  private val preferences: SharedPreferences,
  private val resources: Resources,
  private val dispatchers: AppDispatchers
) : ConnectionRepository {

  override suspend fun save(settings: ConnectionSettingsEntity) = withContext(dispatchers.db) {

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

  override suspend fun delete(settings: ConnectionSettingsEntity) = withContext(dispatchers.db) {
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
  }

  override suspend fun getDefault(): ConnectionSettingsEntity? = withContext(dispatchers.db) {
    val defaultId = defaultId
    if (defaultId < 0) {
      return@withContext null
    }
    return@withContext connectionDao.findById(defaultId)
  }

  override var defaultId: Long
    get() {
      val key = resources.getString(R.string.settings_key_default_index)
      return this.preferences.getLong(key, 0)
    }
    private set(id) {
      val key = resources.getString(R.string.settings_key_default_index)
      this.preferences.edit().putLong(key, id).apply()
    }

  override suspend fun getModel(): ConnectionModel = withContext(dispatchers.db) {
    return@withContext ConnectionModel(defaultId, getAll())
  }

  override suspend fun getAll(): LiveData<List<ConnectionSettingsEntity>> = connectionDao.getAll()

  override suspend fun count(): Long = withContext(dispatchers.db) {
    return@withContext connectionDao.count()
  }
}
