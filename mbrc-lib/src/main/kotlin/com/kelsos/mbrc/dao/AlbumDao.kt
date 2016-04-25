package com.kelsos.mbrc.dao

import com.kelsos.mbrc.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference
import com.raizlabs.android.dbflow.annotation.Index
import com.raizlabs.android.dbflow.annotation.IndexGroup
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

@Table(database = RemoteDatabase::class,
    name = "albums",
    indexGroups = arrayOf(IndexGroup(number = 1, name = "album_name_index")))
class AlbumDao : BaseModel() {
  @PrimaryKey(autoincrement = true) var id: Long = 0
  @Column(name = "name") @Index(indexGroups = intArrayOf(1)) var name: String? = null
  @Column
  @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "artist_id",
      columnType = Long::class,
      referencedFieldIsPrivate = true,
      foreignKeyColumnName = "id")), saveForeignKeyModel = false)
  var artist: ArtistDao? = null

  @Column
  @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "cover_id",
      columnType = Long::class,
      referencedFieldIsPrivate = true,
      foreignKeyColumnName = "id")), saveForeignKeyModel = false)
  var cover: CoverDao? = null

  @Column(name = "date_added") var dateAdded: Long = 0
  @Column(name = "date_updated") var dateUpdated: Long = 0
  @Column(name = "date_deleted") var dateDeleted: Long = 0

}
