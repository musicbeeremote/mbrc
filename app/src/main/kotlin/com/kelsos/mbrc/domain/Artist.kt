package com.kelsos.mbrc.domain

import com.kelsos.mbrc.data.db.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.QueryModel
import com.raizlabs.android.dbflow.structure.BaseQueryModel

@QueryModel(database = RemoteDatabase::class)
class Artist() : BaseQueryModel() {
  @Column var id: Long = 0
  @Column var name: String = ""
}
