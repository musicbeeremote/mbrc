package com.kelsos.mbrc.data.dao

import com.kelsos.mbrc.data.db.RemoteDatabase
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
    name = "tracks",
    indexGroups = arrayOf(IndexGroup(number = 1, name = "track_title_index")))
data class Track(
    @Column @Index(indexGroups = intArrayOf(1)) var title: String = "",
    @Column var path: String = "",
    @Column var year: String = "",
    @Column var position: Int = 0,
    @Column var disc: Int = 0,
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "genre_id",
        columnType = Long::class,
        referencedFieldIsPrivate = true,
        referencedGetterName = "getId",
        referencedSetterName = "setId",
        foreignKeyColumnName = "id")), saveForeignKeyModel = false)
    var genre: Genre? = null,
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "artist_id",
        columnType = Long::class,
        referencedFieldIsPrivate = true,
        referencedGetterName = "getId",
        referencedSetterName = "setId",
        foreignKeyColumnName = "id")), saveForeignKeyModel = false)
    var artist: Artist? = null,
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "album_artist_id",
        columnType = Long::class,
        referencedFieldIsPrivate = true,
        referencedGetterName = "getId",
        referencedSetterName = "setId",
        foreignKeyColumnName = "id")), saveForeignKeyModel = false)
    var albumArtist: Artist? = null,
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "album_id",
        columnType = Long::class,
        referencedFieldIsPrivate = true,
        referencedGetterName = "getId",
        referencedSetterName = "setId",
        foreignKeyColumnName = "id")), saveForeignKeyModel = false)
    var album: Album? = null,

    @Column(name = "date_added") var dateAdded: Long = 0,
    @Column(name = "date_updated") var dateUpdated: Long = 0,
    @Column(name = "date_deleted") var dateDeleted: Long = 0,
    @Column @PrimaryKey(autoincrement = true) var id: Long = 0) : Model {

  override fun load() = modelAdapter<Track>().load(this)

  override fun insert(): Long = modelAdapter<Track>().insert(this)

  override fun save() = modelAdapter<Track>().save(this)

  override fun update() = modelAdapter<Track>().update(this)

  override fun exists(): Boolean = modelAdapter<Track>().exists(this)

  override fun delete() = modelAdapter<Track>().delete(this)
}
