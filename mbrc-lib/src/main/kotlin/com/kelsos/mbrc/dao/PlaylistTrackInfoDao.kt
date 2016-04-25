package com.kelsos.mbrc.dao

import com.kelsos.mbrc.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

@Table(name = "playlist_track_info", database = RemoteDatabase::class)
class PlaylistTrackInfoDao : BaseModel() {

  @PrimaryKey(autoincrement = true)
  var id: Long = 0

  @Column(name = "path")
  var path: String = ""

  @Column(name = "artist")
  var artist: String = ""

  @Column(name = "title")
  var title: String = ""

  @Column(name = "date_added")
  var dateAdded: Long = 0

  @Column(name = "date_updated")
  var dateUpdated: Long = 0

  @Column(name = "date_deleted")
  var dateDeleted: Long = 0
}
