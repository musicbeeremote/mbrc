package com.kelsos.mbrc.dao

import com.kelsos.mbrc.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.structure.BaseModel

@Table(database = RemoteDatabase::class, name = "playlist_track") class PlaylistTrackDao : BaseModel() {
  @Column @PrimaryKey(autoincrement = true) var id: Long = 0
  @Column var position: Int = 0

  @Column
  @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "playlist_id",
      columnType = Long::class,
      referencedFieldIsPrivate = true,
      foreignKeyColumnName = "id")), saveForeignKeyModel = false)
  var playlist: PlaylistDao? = null

  @Column
  @ForeignKey(references = arrayOf(ForeignKeyReference(columnName = "track_info_id",
      columnType = Long::class,
      foreignKeyColumnName = "id",
      referencedFieldIsPrivate = true)), saveForeignKeyModel = false)
  var trackInfo: PlaylistTrackInfoDao? = null

  @Column(name = "date_added")
  var dateAdded: Long = 0

  @Column(name = "date_updated")
  var dateUpdated: Long = 0

  @Column(name = "date_deleted")
  var dateDeleted: Long = 0
}
