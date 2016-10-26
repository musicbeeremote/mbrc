package com.kelsos.mbrc.data.dao

import com.kelsos.mbrc.data.RemoteDatabase
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
data class PlaylistDao(
    @Column @Index(indexGroups = intArrayOf(1)) var name: String = "",
    @Column var path: String = "",
    @Column var tracks: Int = 0,
    @Column(name = "read_only") var readOnly: Boolean = false,
    @Column(name = "date_added") var dateAdded: Long = 0,
    @Column(name = "date_updated") var dateUpdated: Long = 0,
    @Column(name = "date_deleted") var dateDeleted: Long = 0,
    @Column @PrimaryKey(autoincrement = true) var id: Long = 0) : Model {

  override fun load() = modelAdapter<PlaylistDao>().load(this)

  override fun insert(): Long = modelAdapter<PlaylistDao>().insert(this)

  override fun save() = modelAdapter<PlaylistDao>().save(this)

  override fun update() = modelAdapter<PlaylistDao>().update(this)

  override fun exists(): Boolean = modelAdapter<PlaylistDao>().exists(this)

  override fun delete() = modelAdapter<PlaylistDao>().delete(this)
}
