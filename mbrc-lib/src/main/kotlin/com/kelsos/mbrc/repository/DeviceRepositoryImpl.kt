package com.kelsos.mbrc.repository

import com.kelsos.mbrc.SettingsDatabase
import com.kelsos.mbrc.domain.DeviceSettings
import com.kelsos.mbrc.domain.DeviceSettings_Table
import com.raizlabs.android.dbflow.runtime.TransactionManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable

class DeviceRepositoryImpl : DeviceRepository {
  override fun getPageObservable(offset: Int, limit: Int): Observable<List<DeviceSettings>> {
    return Observable.defer { Observable.just(getPage(offset, limit)) }
  }

  override val allObservable: Observable<List<DeviceSettings>>
    get() = Observable.defer { Observable.just(all) }

  override fun getPage(offset: Int, limit: Int): List<DeviceSettings> {
    return SQLite.select().from(DeviceSettings::class.java).limit(limit).offset(offset).queryList()
  }

  override val all: List<DeviceSettings>
    get() = SQLite.select().from(DeviceSettings::class.java).queryList()

  override fun getById(id: Long): DeviceSettings {
    return SQLite.select().from(DeviceSettings::class.java).where(DeviceSettings_Table.id.eq(id)).querySingle()
  }

  override fun save(items: List<DeviceSettings>) {
    TransactionManager.transact(SettingsDatabase.NAME) { Observable.from(items).forEach({ it.save() }) }
  }

  override fun save(item: DeviceSettings) {
    item.save()
  }

  override fun count(): Long {
    return SQLite.selectCountOf().from(DeviceSettings::class.java).count()
  }
}
