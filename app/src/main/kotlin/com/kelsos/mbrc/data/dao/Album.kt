package com.kelsos.mbrc.data.dao

import com.kelsos.mbrc.data.db.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.Model

@Table(database = RemoteDatabase::class,
    name = "albums",
    indexGroups = arrayOf(IndexGroup(number = 1, name = "album_name_index")))
data class Album(
    @Column(name = "name") @Index(indexGroups = intArrayOf(1)) var name: String = "",
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "artist_id",
        foreignKeyColumnName = "id")), saveForeignKeyModel = false)
    var artist: Artist? = null,
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "cover_id",
        foreignKeyColumnName = "id")), saveForeignKeyModel = false)
    var cover: Cover? = null,
    @Column(name = "date_added") var dateAdded: Long = 0,
    @Column(name = "date_updated") var dateUpdated: Long = 0,
    @Column(name = "date_deleted") var dateDeleted: Long = 0,
    @PrimaryKey(autoincrement = true) var id: Long = 0) : Model {

  override fun load() = modelAdapter<Album>().load(this)

  override fun insert(): Long = modelAdapter<Album>().insert(this)

  override fun save() = modelAdapter<Album>().save(this)

  override fun update() = modelAdapter<Album>().update(this)

  override fun exists(): Boolean = modelAdapter<Album>().exists(this)

  override fun delete() = modelAdapter<Album>().delete(this)
}
