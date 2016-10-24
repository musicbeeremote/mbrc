package com.kelsos.mbrc.repository.library

import com.kelsos.mbrc.data.RemoteDatabase
import com.kelsos.mbrc.data.dao.GenreDao
import com.kelsos.mbrc.data.GenreDao_Table
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.OrderBy
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable

class GenreRepositoryImpl : GenreRepository {
    override fun getPageObservable(offset: Int, limit: Int): Observable<List<GenreDao>> {
        return Observable.defer { Observable.just(getPage(offset, limit)) }
    }

    override fun getAllObservable(): Observable<List<GenreDao>> = Observable.defer {
        Observable.just(getAll())
    }

    override fun getPage(offset: Int, limit: Int): List<GenreDao> {
        return SQLite.select()
                .from(GenreDao::class.java)
                .where()
                .offset(offset)
                .orderBy(OrderBy.fromProperty(GenreDao_Table.name).ascending())
                .limit(limit)
                .queryList()
    }

    override fun getAll(): List<GenreDao> = SQLite.select()
            .from(GenreDao::class.java)
            .where()
            .orderBy(OrderBy.fromProperty(GenreDao_Table.name)
                    .ascending())
            .queryList()

    override fun getById(id: Long): GenreDao? {
        return SQLite.select()
                .from(GenreDao::class.java)
                .where(GenreDao_Table.id.eq(id))
                .querySingle()
    }

    override fun save(items: List<GenreDao>) {
        FlowManager.getDatabase(RemoteDatabase::class.java).executeTransaction {
            Observable.from(items).forEach({ it.save() })
        }
    }

    override fun save(item: GenreDao) {
        item.save()
    }

    override fun count(): Long {
        return SQLite.selectCountOf().from(GenreDao::class.java).count()
    }
}
