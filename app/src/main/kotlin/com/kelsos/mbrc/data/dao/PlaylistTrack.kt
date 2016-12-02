package com.kelsos.mbrc.data.dao

import com.kelsos.mbrc.data.db.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.Model

@Table(database = RemoteDatabase::class, name = "playlist_track")
data class PlaylistTrack(
    @Column var position: Int = 0,
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "playlist_id",
        columnType = Long::class,
        referencedFieldIsPrivate = true,
        referencedGetterName = "getId",
        referencedSetterName = "setId",
        foreignKeyColumnName = "id")), saveForeignKeyModel = false)
    var playlist: Playlist? = null,
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "track_info_id",
        columnType = Long::class,
        foreignKeyColumnName = "id",
        referencedGetterName = "getId",
        referencedSetterName = "setId",
        referencedFieldIsPrivate = true)), saveForeignKeyModel = false)
    var trackInfo: PlaylistTrackInfo? = null,
    @Column(name = "date_added") var dateAdded: Long = 0,
    @Column(name = "date_updated") var dateUpdated: Long = 0,
    @Column(name = "date_deleted") var dateDeleted: Long = 0,
    @Column @PrimaryKey(autoincrement = true) var id: Long = 0
) : Model {

  override fun load() = modelAdapter<PlaylistTrack>().load(this)

  override fun insert(): Long = modelAdapter<PlaylistTrack>().insert(this)

  override fun save() = modelAdapter<PlaylistTrack>().save(this)

  override fun update() = modelAdapter<PlaylistTrack>().update(this)

  override fun exists(): Boolean = modelAdapter<PlaylistTrack>().exists(this)

  override fun delete() = modelAdapter<PlaylistTrack>().delete(this)
}
