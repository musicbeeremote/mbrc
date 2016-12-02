package com.kelsos.mbrc.domain

import com.kelsos.mbrc.data.db.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.QueryModel
import com.raizlabs.android.dbflow.structure.BaseQueryModel

@QueryModel(database = RemoteDatabase::class)
class Track() : BaseQueryModel() {
  @Column var id: Long = 0
  @Column var artist: String = ""
  @Column var title: String = ""
  @Column var cover: String = ""
}
