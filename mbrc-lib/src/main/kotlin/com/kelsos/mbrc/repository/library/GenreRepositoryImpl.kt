package com.kelsos.mbrc.repository.library

import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.dao.GenreDao
import com.kelsos.mbrc.dao.GenreDao_Table
import com.raizlabs.android.dbflow.runtime.TransactionManager
import com.raizlabs.android.dbflow.sql.language.OrderBy
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModel
import rx.Observable
import rx.functions.Action1

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

    override fun getById(id: Long): GenreDao {
        return SQLite.select()
                .from(GenreDao::class.java)
                .where(GenreDao_Table.id.eq(id))
                .querySingle()
    }

    override fun save(items: List<GenreDao>) {
        TransactionManager.transact(RemoteDatabase.NAME) {
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
