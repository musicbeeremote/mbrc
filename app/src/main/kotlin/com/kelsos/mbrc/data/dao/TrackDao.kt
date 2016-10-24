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
    name = "tracks",
    indexGroups = arrayOf(IndexGroup(number = 1, name = "track_title_index")))
data class TrackDao(
    @Column @Index(indexGroups = intArrayOf(1)) var title: String,
    @Column var path: String,
    @Column var year: String,
    @Column var position: Int = 0,
    @Column var disc: Int = 0,
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "genre_id",
        columnType = Long::class,
        referencedFieldIsPrivate = true,
        foreignKeyColumnName = "id")), saveForeignKeyModel = false)
    var genre: GenreDao? = null,
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "artist_id",
        columnType = Long::class,
        referencedFieldIsPrivate = true,
        foreignKeyColumnName = "id")), saveForeignKeyModel = false)
    var artist: ArtistDao? = null,
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "album_artist_id",
        columnType = Long::class,
        referencedFieldIsPrivate = true,
        foreignKeyColumnName = "id")), saveForeignKeyModel = false)
    var albumArtist: ArtistDao? = null,
    @Column
    @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "album_id",
        columnType = Long::class,
        referencedFieldIsPrivate = true,
        foreignKeyColumnName = "id")), saveForeignKeyModel = false)
    var album: AlbumDao? = null,

    @Column(name = "date_added") var dateAdded: Long = 0,
    @Column(name = "date_updated") var dateUpdated: Long = 0,
    @Column(name = "date_deleted") var dateDeleted: Long = 0,
    @Column @PrimaryKey(autoincrement = true) var id: Long = 0) : Model {

  override fun load() = modelAdapter<TrackDao>().load(this)

  override fun insert(): Long = modelAdapter<TrackDao>().insert(this)

  override fun save() = modelAdapter<TrackDao>().save(this)

  override fun update() = modelAdapter<TrackDao>().update(this)

  override fun exists(): Boolean = modelAdapter<TrackDao>().exists(this)

  override fun delete() = modelAdapter<TrackDao>().delete(this)
}
