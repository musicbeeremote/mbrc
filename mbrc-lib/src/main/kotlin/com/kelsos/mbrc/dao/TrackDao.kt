package com.kelsos.mbrc.dao

import com.kelsos.mbrc.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.structure.BaseModel

@Table(database = RemoteDatabase::class,
    name = "tracks",
    indexGroups = arrayOf(IndexGroup(number = 1, name = "track_title_index")))
class TrackDao : BaseModel() {
  @Column
  @PrimaryKey(autoincrement = true)
  var id: Long = 0

  @Column
  @Index(indexGroups = intArrayOf(1)) var title: String = ""

  @Column var position: Int = 0

  @Column
  @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "genre_id",
      columnType = Long::class,
      referencedFieldIsPrivate = true,
      foreignKeyColumnName = "id")),
      saveForeignKeyModel = false) var genre: GenreDao? = null

  @Column
  @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "artist_id",
      columnType = Long::class,
      referencedFieldIsPrivate = true,
      foreignKeyColumnName = "id")), saveForeignKeyModel = false) var artist: ArtistDao? = null

  @Column
  @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "album_artist_id",
      columnType = Long::class,
      referencedFieldIsPrivate = true,
      foreignKeyColumnName = "id")), saveForeignKeyModel = false) var albumArtist: ArtistDao? = null

  @Column
  @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "album_id",
      columnType = Long::class,
      referencedFieldIsPrivate = true,
      foreignKeyColumnName = "id")), saveForeignKeyModel = false) var album: AlbumDao? = null

  @Column
  var year: String = ""

  @Column
  var path: String = ""

  @Column(name = "date_added")
  var dateAdded: Long = 0

  @Column(name = "date_updated")
  var dateUpdated: Long = 0

  @Column(name = "date_deleted")
  var dateDeleted: Long = 0

  @Column
  var disc: Int = 0
}
