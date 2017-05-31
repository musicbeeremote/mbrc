package com.kelsos.mbrc.networking.connections

import android.content.SharedPreferences
import android.content.res.Resources

import com.kelsos.mbrc.R
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite

import javax.inject.Inject

class ConnectionRepositoryImpl
@Inject constructor(
    private val preferences: SharedPreferences,
    private val resources: Resources
) : ConnectionRepository {

  override fun save(settings: ConnectionSettings) {
    settings.save()

    if (count() == 1L) {
      default = last
    }
  }

  override fun delete(settings: ConnectionSettings) {
    val oldId = settings.id

    settings.delete()

    if (oldId != defaultId) {
      return
    }

    val count = count()
    if (count == 0L) {
      defaultId = -1
    } else {
      val before = getItemBefore(oldId)
      if (before != null) {
        default = before
      } else {
        default = first
      }
    }
  }

  private fun getItemBefore(id: Long): ConnectionSettings? {
    return SQLite.select()
        .from(ConnectionSettings::class.java)
        .where(ConnectionSettings_Table.id.lessThan(id))
        .orderBy(ConnectionSettings_Table.id, false)
        .querySingle()
  }

  private val first: ConnectionSettings?
    get() = SQLite.select()
        .from(ConnectionSettings::class.java)
        .orderBy(ConnectionSettings_Table.id, true)
        .querySingle()

  private val last: ConnectionSettings?
    get() = SQLite.select()
        .from(ConnectionSettings::class.java)
        .orderBy(ConnectionSettings_Table.id, false)
        .querySingle()

  override fun update(settings: ConnectionSettings) {
    settings.update()
  }

  override var default: ConnectionSettings?
    get() {
      val defaultId = defaultId
      if (defaultId < 0) {
        return null
      }

      return SQLite.select()
          .from(ConnectionSettings::class.java)
          .where(ConnectionSettings_Table.id.`is`(defaultId))
          .querySingle()
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

  override val all: List<ConnectionSettings>
    get() = SQLite.select().from(ConnectionSettings::class.java).queryList()

  override fun count(): Long {
    return SQLite.selectCountOf().from(ConnectionSettings::class.java).count()
  }

}
