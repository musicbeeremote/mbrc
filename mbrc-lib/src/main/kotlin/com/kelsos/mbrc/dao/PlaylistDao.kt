package com.kelsos.mbrc.dao

import com.kelsos.mbrc.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.structure.BaseModel

@Table(database = RemoteDatabase::class,
    name = "playlists",
    indexGroups = arrayOf(IndexGroup(number = 1, name = "playlist_name_index")))
class PlaylistDao : BaseModel() {

  @Column
  @PrimaryKey(autoincrement = true)
  var id: Long = 0

  @Column
  @Index(indexGroups = intArrayOf(1)) var name: String? = null

  @Column(name = "read_only")
  var readOnly: Boolean = false

  @Column
  var path: String = ""

  @Column
  var tracks: Int = 0

  @Column(name = "date_added")
  var dateAdded: Long = 0

  @Column(name = "date_updated")
  var dateUpdated: Long = 0

  @Column(name = "date_deleted")
  var dateDeleted: Long = 0

}
