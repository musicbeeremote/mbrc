package com.kelsos.mbrc.data.dao

import com.kelsos.mbrc.data.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.Model

@Table(name = "playlist_track_info",
    database = RemoteDatabase::class)
data class PlaylistTrackInfoDao(
    @PrimaryKey(autoincrement = true) var id: Long = 0,
    @Column(name = "path") var path: String = "",
    @Column(name = "artist") var artist: String = "",
    @Column(name = "title") var title: String = "",
    @Column(name = "date_added") var dateAdded: Long = 0,
    @Column(name = "date_updated") var dateUpdated: Long = 0,
    @Column(name = "date_deleted") var dateDeleted: Long = 0) : Model {

  override fun load() = modelAdapter<PlaylistTrackInfoDao>().load(this)

  override fun insert(): Long = modelAdapter<PlaylistTrackInfoDao>().insert(this)

  override fun save() = modelAdapter<PlaylistTrackInfoDao>().save(this)

  override fun update() = modelAdapter<PlaylistTrackInfoDao>().update(this)

  override fun exists(): Boolean = modelAdapter<PlaylistTrackInfoDao>().exists(this)

  override fun delete() = modelAdapter<PlaylistTrackInfoDao>().delete(this)
}
