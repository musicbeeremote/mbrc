package com.kelsos.mbrc.repository.library

import android.text.TextUtils
import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.dao.AlbumDao
import com.kelsos.mbrc.dao.AlbumDao_Table
import com.kelsos.mbrc.dao.TrackDao
import com.kelsos.mbrc.dao.TrackDao_Table
import com.kelsos.mbrc.dao.views.AlbumModelView
import com.kelsos.mbrc.dao.views.AlbumModelView_ViewTable
import com.kelsos.mbrc.dao.views.ArtistAlbumView
import com.kelsos.mbrc.dao.views.ArtistAlbumView_ViewTable
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable

class AlbumRepositoryImpl : AlbumRepository {
    override fun getPageObservable(offset: Int, limit: Int): Observable<List<AlbumDao>> {
        return Observable.defer { Observable.just(getPage(offset, limit)) }
    }

    override fun getAllObservable(): Observable<List<AlbumDao>> = Observable.defer {
        Observable.just(getAll())
    }

    override fun getPage(offset: Int, limit: Int): List<AlbumDao> {
        return SQLite.select().from(AlbumDao::class.java).limit(limit).offset(offset).queryList()
    }

    override fun getAll(): List<AlbumDao> = SQLite.select().from(AlbumDao::class.java).queryList()

    override fun getById(id: Long): AlbumDao? {
        return SQLite.select().from(AlbumDao::class.java).where(AlbumDao_Table.id.eq(id)).querySingle()
    }

    override fun save(items: List<AlbumDao>) {
        FlowManager.getDatabase(RemoteDatabase::class.java).executeTransaction {
            Observable.from(items).forEach({ it.save() })
        }
    }

    override fun save(item: AlbumDao) {
        item.save()
    }

    override fun count(): Long {
        return SQLite.selectCountOf().from(AlbumDao::class.java).count()
    }

    override fun getAlbumViewById(albumId: Int): AlbumModelView? {
        return SQLite.select()
                .from(AlbumModelView::class.java)
                .where(AlbumModelView_ViewTable.id.`is`(albumId.toLong()))
                .querySingle()
    }

    override fun getAlbumViews(offset: Int, limit: Int): Observable<List<AlbumModelView>> {
        return Observable.defer {
            Observable.just(SQLite.select().from(AlbumModelView::class.java).queryList())
        }
    }

    override fun getAlbumYear(id: Long): String {
        val trackDao = SQLite.select(TrackDao_Table.year)
                .distinct()
                .from(TrackDao::class.java)
                .where(TrackDao_Table.album_id.eq(id))
                .groupBy(TrackDao_Table.year)
                .querySingle()

        if (trackDao == null) {
            return ""
        } else {
            return if (TextUtils.isEmpty(trackDao.year)) "" else trackDao.year
        }
    }

    override fun getAlbumsByArtist(artistId: Long): Observable<List<ArtistAlbumView>> {
        return Observable.defer {
            Observable.just(SQLite.select()
                    .from(ArtistAlbumView::class.java)
                    .where(ArtistAlbumView_ViewTable.artist_id.eq(artistId))
                    .queryList())
        }
    }
}
