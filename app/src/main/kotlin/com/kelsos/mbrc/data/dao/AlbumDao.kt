package com.kelsos.mbrc.data.dao

import com.kelsos.mbrc.data.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference
import com.raizlabs.android.dbflow.annotation.Index
import com.raizlabs.android.dbflow.annotation.IndexGroup
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.Model

@Table(database = RemoteDatabase::class,
    name = "albums",
    indexGroups = arrayOf(IndexGroup(number = 1, name = "album_name_index")))
data class AlbumDao(
    @Column(name = "name") @Index(indexGroups = intArrayOf(1)) var name: String = "",
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "artist_id",
        columnType = Long::class,
        referencedFieldIsPrivate = true,
        referencedGetterName = "getId",
        referencedSetterName = "setId",
        foreignKeyColumnName = "id")), saveForeignKeyModel = false)
    var artist: ArtistDao? = null,
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "cover_id",
        columnType = Long::class,
        referencedFieldIsPrivate = true,
        referencedGetterName = "getId",
        referencedSetterName = "setId",
        foreignKeyColumnName = "id")), saveForeignKeyModel = false)
    var cover: CoverDao? = null,
    @Column(name = "date_added") var dateAdded: Long = 0,
    @Column(name = "date_updated") var dateUpdated: Long = 0,
    @Column(name = "date_deleted") var dateDeleted: Long = 0,
    @PrimaryKey(autoincrement = true) var id: Long = 0) : Model {

  override fun load() = modelAdapter<AlbumDao>().load(this)

  override fun insert(): Long = modelAdapter<AlbumDao>().insert(this)

  override fun save() = modelAdapter<AlbumDao>().save(this)

  override fun update() = modelAdapter<AlbumDao>().update(this)

  override fun exists(): Boolean = modelAdapter<AlbumDao>().exists(this)

  override fun delete() = modelAdapter<AlbumDao>().delete(this)
}
