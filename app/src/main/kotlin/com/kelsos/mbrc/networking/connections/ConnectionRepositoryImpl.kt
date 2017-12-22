package com.kelsos.mbrc.networking.connections

import android.content.SharedPreferences
import android.content.res.Resources
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.connectionmanager.ConnectionModel
import io.reactivex.Single
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import javax.inject.Inject

class ConnectionRepositoryImpl
@Inject
constructor(
    private val connectionDao: ConnectionDao,
    private val preferences: SharedPreferences,
    private val resources: Resources
) : ConnectionRepository {

  override fun save(settings: ConnectionSettingsEntity) {
    async(CommonPool) {
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
    async(CommonPool) {
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

  override fun getModel(): Single<ConnectionModel> = Single.fromCallable {
    return@fromCallable ConnectionModel(defaultId, getAll())
  }

  override fun getAll(): List<ConnectionSettingsEntity> = connectionDao.getAll()

  override fun count(): Long {
    return connectionDao.count()
  }

}
