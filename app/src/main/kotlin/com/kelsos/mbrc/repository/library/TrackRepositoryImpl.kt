package com.kelsos.mbrc.repository.library

import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.dao.TrackDao
import com.kelsos.mbrc.dao.TrackDao_Table
import com.kelsos.mbrc.dao.views.TrackModelView
import com.kelsos.mbrc.dao.views.TrackModelView_ViewTable
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable

class TrackRepositoryImpl : TrackRepository {
    override fun getPageObservable(offset: Int, limit: Int): Observable<List<TrackDao>> {
        return Observable.defer { Observable.just(getPage(offset, limit)) }
    }

    override fun getAllObservable(): Observable<List<TrackDao>> = Observable.defer { Observable.just(getAll()) }

    override fun getPage(offset: Int, limit: Int): List<TrackDao> {
        return SQLite.select()
                .from(TrackDao::class.java)
                .limit(limit)
                .offset(offset)
                .queryList()
    }

    override fun getAll(): List<TrackDao> = SQLite.select()
            .from(TrackDao::class.java)
            .queryList()

    override fun getById(id: Long): TrackDao? {
        return SQLite.select()
                .from(TrackDao::class.java)
                .where(TrackDao_Table.id.eq(id))
                .querySingle()
    }

    override fun save(items: List<TrackDao>) {
        FlowManager.getDatabase(RemoteDatabase::class.java).executeTransaction {
            Observable.from(items).forEach({ it.save() })
        }
    }

    override fun save(item: TrackDao) {
        item.save()
    }

    override fun count(): Long {
        return SQLite.selectCountOf().from(TrackDao::class.java).count()
    }

    override fun getTracksByAlbumId(albumId: Long): Observable<List<TrackModelView>> {
        return Observable.just(SQLite.select()
                .from(TrackModelView::class.java)
                .where(TrackModelView_ViewTable.album_id.`is`(albumId))
                .queryList())
    }

    override fun getTracks(offset: Int, limit: Int): Observable<List<TrackModelView>> {
        return Observable.defer {
            Observable.just(SQLite.select()
                    .from(TrackModelView::class.java)
                    .where()
                    .limit(limit)
                    .offset(offset)
                    .queryList())
        }
    }
}
