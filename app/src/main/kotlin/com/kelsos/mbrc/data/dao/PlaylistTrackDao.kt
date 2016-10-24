package com.kelsos.mbrc.data.dao

import com.kelsos.mbrc.data.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.Model

@Table(database = RemoteDatabase::class, name = "playlist_track")
data class PlaylistTrackDao(
    @Column var position: Int = 0,
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "playlist_id",
        columnType = Long::class,
        referencedFieldIsPrivate = true,
        foreignKeyColumnName = "id")), saveForeignKeyModel = false)
    var playlist: PlaylistDao? = null,
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "track_info_id",
        columnType = Long::class,
        foreignKeyColumnName = "id",
        referencedFieldIsPrivate = true)), saveForeignKeyModel = false)
    var trackInfo: PlaylistTrackInfoDao? = null,
    @Column(name = "date_added") var dateAdded: Long = 0,
    @Column(name = "date_updated") var dateUpdated: Long = 0,
    @Column(name = "date_deleted") var dateDeleted: Long = 0,
    @Column @PrimaryKey(autoincrement = true) var id: Long = 0
) : Model {

  override fun load() = modelAdapter<PlaylistTrackDao>().load(this)

  override fun insert(): Long = modelAdapter<PlaylistTrackDao>().insert(this)

  override fun save() = modelAdapter<PlaylistTrackDao>().save(this)

  override fun update() = modelAdapter<PlaylistTrackDao>().update(this)

  override fun exists(): Boolean = modelAdapter<PlaylistTrackDao>().exists(this)

  override fun delete() = modelAdapter<PlaylistTrackDao>().delete(this)
}
