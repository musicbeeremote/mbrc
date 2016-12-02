package com.kelsos.mbrc.data.dao

import com.kelsos.mbrc.data.db.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.Index
import com.raizlabs.android.dbflow.annotation.IndexGroup
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.Model

@Table(database = RemoteDatabase::class,
    name = "playlists",
    useBooleanGetterSetters = false,
    indexGroups = arrayOf(IndexGroup(number = 1, name = "playlist_name_index")))
data class Playlist(
    @Column @Index(indexGroups = intArrayOf(1)) var name: String = "",
    @Column var path: String = "",
    @Column var tracks: Int = 0,
    @Column(name = "read_only") var readOnly: Boolean = false,
    @Column(name = "date_added") var dateAdded: Long = 0,
    @Column(name = "date_updated") var dateUpdated: Long = 0,
    @Column(name = "date_deleted") var dateDeleted: Long = 0,
    @Column @PrimaryKey(autoincrement = true) var id: Long = 0) : Model {

  override fun load() = modelAdapter<Playlist>().load(this)

  override fun insert(): Long = modelAdapter<Playlist>().insert(this)

  override fun save() = modelAdapter<Playlist>().save(this)

  override fun update() = modelAdapter<Playlist>().update(this)

  override fun exists(): Boolean = modelAdapter<Playlist>().exists(this)

  override fun delete() = modelAdapter<Playlist>().delete(this)
}
