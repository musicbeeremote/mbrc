package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.SettingsDatabase
import com.kelsos.mbrc.data.dao.DeviceSettings
import com.kelsos.mbrc.data.dao.DeviceSettings_Table
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable
import rx.lang.kotlin.toSingletonObservable

class DeviceRepositoryImpl : DeviceRepository {
  override fun getPageObservable(offset: Int, limit: Int): Observable<List<DeviceSettings>> {
    return getPage(offset, limit).toSingletonObservable()
  }

  override fun getAllObservable(): Observable<List<DeviceSettings>> = getAll().toSingletonObservable()


  override fun getPage(offset: Int, limit: Int): List<DeviceSettings> {
    return SQLite.select()
        .from(DeviceSettings::class.java)
        .limit(limit)
        .offset(offset)
        .queryList()
  }

  override fun getAll(): List<DeviceSettings> = SQLite.select()
      .from(DeviceSettings::class.java)
      .queryList()

  override fun getById(id: Long): DeviceSettings? {
    return SQLite.select()
        .from(DeviceSettings::class.java)
        .where(DeviceSettings_Table.id.eq(id))
        .querySingle()
  }

  override fun save(items: List<DeviceSettings>) {
    FlowManager.getDatabase(SettingsDatabase::class.java).executeTransaction {
      items.forEach { it.save() }
    }
  }

  override fun save(item: DeviceSettings) {
    item.save()
  }

  override fun count(): Long {
    return SQLite.selectCountOf().from(DeviceSettings::class.java).count()
  }
}
