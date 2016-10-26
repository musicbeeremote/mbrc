package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.SettingsDatabase
import com.kelsos.mbrc.data.dao.ConnectionSettings
import com.kelsos.mbrc.data.dao.ConnectionSettings_Table
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable
import rx.lang.kotlin.toSingletonObservable
import javax.inject.Inject

class ConnectionRepositoryImpl
@Inject constructor() : ConnectionRepository {
  override fun getPageObservable(offset: Int, limit: Int): Observable<List<ConnectionSettings>> {
    return getPage(offset, limit).toSingletonObservable()
  }

  override fun getAllObservable(): Observable<List<ConnectionSettings>> = getAll().toSingletonObservable()


  override fun getPage(offset: Int, limit: Int): List<ConnectionSettings> {
    return SQLite.select()
        .from(ConnectionSettings::class.java)
        .limit(limit)
        .offset(offset)
        .queryList()
  }

  override fun getAll(): List<ConnectionSettings> = SQLite.select()
      .from(ConnectionSettings::class.java)
      .queryList()

  override fun getById(id: Long): ConnectionSettings? {
    return SQLite.select()
        .from(ConnectionSettings::class.java)
        .where(ConnectionSettings_Table.id.eq(id))
        .querySingle()
  }

  override fun save(items: List<ConnectionSettings>) {
    FlowManager.getDatabase(SettingsDatabase::class.java).executeTransaction {
      items.forEach { it.save() }
    }
  }

  override fun save(item: ConnectionSettings) {
    item.save()
  }

  override fun count(): Long {
    return SQLite.selectCountOf().from(ConnectionSettings::class.java).count()
  }
}
