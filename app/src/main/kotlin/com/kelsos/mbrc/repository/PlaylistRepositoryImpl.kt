package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.RemoteDatabase
import com.kelsos.mbrc.data.dao.PlaylistDao
import com.kelsos.mbrc.data.dao.PlaylistDao_Table
import com.kelsos.mbrc.data.dao.PlaylistTrackDao
import com.kelsos.mbrc.data.dao.PlaylistTrackInfoDao
import com.kelsos.mbrc.data.dao.PlaylistTrackInfoDao_Table
import com.kelsos.mbrc.data.views.PlaylistTrackView
import com.kelsos.mbrc.data.views.PlaylistTrackView_ViewTable
import com.kelsos.mbrc.domain.Playlist
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo
import com.kelsos.mbrc.mappers.PlaylistMapper
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.sql.language.Select
import rx.Observable
import rx.Subscriber
import rx.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class PlaylistRepositoryImpl
@Inject constructor() : PlaylistRepository {

  override fun getPlaylists(): Observable<List<Playlist>> = Observable.create { subscriber: Subscriber<in List<Playlist>> ->
    val playlistDaos = Select().from(PlaylistDao::class.java).queryList()
    subscriber.onNext(PlaylistMapper.mapData(playlistDaos))
    subscriber.onCompleted()
  }.subscribeOn(Schedulers.io())

  override fun getUserPlaylists(): Observable<List<Playlist>> = Observable.create { subscriber: Subscriber<in List<Playlist>> ->
    val playlistDaos = SQLite.select().from(PlaylistDao::class.java).where(PlaylistDao_Table.read_only.eq(
        false)).queryList()
    subscriber.onNext(PlaylistMapper.mapData(playlistDaos))
    subscriber.onCompleted()
  }.subscribeOn(Schedulers.io())

  override fun savePlaylists(playlists: List<PlaylistDao>) {
    FlowManager.getDatabase(RemoteDatabase::class.java)
        .executeTransaction {
          Observable.from(playlists).forEach { value ->
            if (value.dateDeleted > 0) {
              value.delete()
            } else {
              value.save()
            }
          }
        }
  }

  override fun getPlaylistTracks(playlistId: Long): Observable<List<PlaylistTrackView>> {
    val list = SQLite.select().from(PlaylistTrackView::class.java).where(PlaylistTrackView_ViewTable.playlist_id.eq(
        playlistId)).queryList()

    return Observable.just(list)
  }

  override fun getTrackInfo(): Observable<List<PlaylistTrackInfo>> = Observable.empty()

  override fun savePlaylistTrackInfo(data: List<PlaylistTrackInfoDao>) {
    FlowManager.getDatabase(RemoteDatabase::class.java)
        .executeTransaction {
          Observable.from(data).subscribeOn(Schedulers.immediate()).observeOn(Schedulers.immediate()).subscribe(
              { info ->
                if (info.dateDeleted > 0) {
                  info.delete()
                } else {
                  info.save()
                }

              }) {

          }
        }
  }

  override fun savePlaylistTracks(data: List<PlaylistTrackDao>) {
    FlowManager.getDatabase(RemoteDatabase::class.java)
        .executeTransaction {
          Observable.from(data).subscribeOn(Schedulers.immediate())
              .observeOn(Schedulers.immediate())
              .subscribe({
                if (it.dateDeleted > 0) {
                  it.delete()
                } else {
                  it.save()
                }

              }) { Timber.e(it, "Failed to save playlist tracks") }
        }
  }

  override fun getPlaylistById(id: Long): PlaylistDao? {
    return SQLite.select().from(PlaylistDao::class.java).where(PlaylistDao_Table.id.`is`(id)).querySingle()
  }

  override fun getTrackInfoById(id: Long): PlaylistTrackInfoDao? {
    return SQLite.select().from(PlaylistTrackInfoDao::class.java).where(
        PlaylistTrackInfoDao_Table.id.`is`(id)).querySingle()
  }
}
