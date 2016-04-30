package com.kelsos.mbrc.dao

import com.kelsos.mbrc.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

@Table(database = RemoteDatabase::class, name = "queue_tracks")
class QueueTrackDao : BaseModel() {
  @Column
  @PrimaryKey(autoincrement = true)
  var id: Long = 0

  @Column
  var artist: String = ""

  @Column
  var title: String = ""

  @Column
  var path: String = ""

  @Column
  var position: Int = 0
}
