package com.kelsos.mbrc.repository.library

import com.kelsos.mbrc.data.RemoteDatabase
import com.kelsos.mbrc.data.dao.CoverDao
import com.kelsos.mbrc.data.TrackDao_Table
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable

class CoverRepositoryImpl : CoverRepository {
    override fun getPageObservable(offset: Int, limit: Int): Observable<List<CoverDao>> {
        return Observable.defer { Observable.just(getPage(offset, limit)) }
    }

    override fun getAllObservable(): Observable<List<CoverDao>> = Observable.defer {
        Observable.just(getAll())
    }

    override fun getPage(offset: Int, limit: Int): List<CoverDao> {
        return SQLite.select()
                .from(CoverDao::class.java)
                .limit(limit)
                .offset(offset)
                .queryList()
    }

    override fun getAll(): List<CoverDao> = SQLite.select()
            .from(CoverDao::class.java)
            .queryList()

    override fun getById(id: Long): CoverDao? {
        return SQLite.select()
                .from(CoverDao::class.java)
                .where(TrackDao_Table.id.eq(id))
                .querySingle()
    }


    override fun save(item: CoverDao) {
        item.save()
    }

    override fun count(): Long {
        return SQLite.selectCountOf()
                .from(CoverDao::class.java)
                .count()
    }

    override fun save(items: List<CoverDao>) {
        FlowManager.getDatabase(RemoteDatabase::class.java).executeTransaction {
            Observable.from(items).forEach({ it.save() })
        }
    }
}
